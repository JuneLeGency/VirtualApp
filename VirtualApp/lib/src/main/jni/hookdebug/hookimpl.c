//
// Created by legency on 2018/6/1.
//

#undef _GNU_SOURCE
#define _GNU_SOURCE

#include <IHook/hook.h>
#include <pthread.h>
#include <sys/socket.h>

#include <unistd.h>
#include <asm/unistd.h>
#include <android/log.h>
#include <arpa/inet.h>
#include <sys/uio.h>
#include <netdb.h>
#include <stdio.h>
#include <malloc.h>
#include <unwind.h>
#include <dlfcn.h>

#define HOOK_DEF(ret, func, ...) \
  ret (*orig_##func)(__VA_ARGS__); \
  ret custom_##func(__VA_ARGS__)

#define SETUP_SYM(name) do { \
    i_hook(name, (void *) custom_ ## name, (void **) &orig_ ## name); \
} while(0)

#define HOOK_DONE() do{i_hook_done();}while(0)

pthread_once_t init_once = PTHREAD_ONCE_INIT;
#define INIT() init_lib_wrapper(__FUNCTION__)

void android_log(int level, const char *tag, const char *fmt, ...) {
    va_list ap;
    char buf[100];
    va_start(ap, fmt);
    vsnprintf(buf, sizeof(buf), fmt, ap);
    va_end(ap);
    __android_log_print(level, tag, "%s", buf);
}

#define ALOGE(...)  android_log(ANDROID_LOG_DEBUG, "lol", __VA_ARGS__);


void dumpBacktraceIndex(char *out, intptr_t *buffer, size_t count) {
    for (size_t idx = 0; idx < count; ++idx) {
        intptr_t addr = buffer[idx];
        const char *symbol = "      ";
        const char *dlfile = "      ";

        Dl_info info;
        if (dladdr((void *) addr, &info)) {
            if (info.dli_sname) {
                symbol = info.dli_sname;
            }
            if (info.dli_fname) {
                dlfile = info.dli_fname;
            }
        } else {
            strcat(out, "#                               \n");
            continue;
        }
        char temp[50];
        memset(temp, 0, sizeof(temp));
        sprintf(temp, "%zu", idx);
        strcat(out, "#");
        strcat(out, temp);
        strcat(out, ": ");
        memset(temp, 0, sizeof(temp));
        sprintf(temp, "0x%x", addr);
        strcat(out, temp);
        strcat(out, "  ");
        strcat(out, symbol);
        strcat(out, "      ");
        strcat(out, dlfile);
        strcat(out, "\n");
    }
}

struct BacktraceState {
    intptr_t *current;
    intptr_t *end;
};

static _Unwind_Reason_Code unwindCallback(struct _Unwind_Context *context, void *arg) {
    struct BacktraceState *state = (struct BacktraceState *) arg;
    intptr_t ip = (intptr_t) _Unwind_GetIP(context);
    if (ip) {
        if (state->current == state->end) {
            return _URC_END_OF_STACK;
        } else {
            state->current[0] = ip;
            state->current++;
        }
    }
    return _URC_NO_REASON;
}

size_t captureBacktrace(intptr_t *buffer, size_t maxStackDeep) {
    struct BacktraceState state = {buffer, buffer + maxStackDeep};
    _Unwind_Backtrace(unwindCallback, &state);
    return state.current - buffer;
}

void backtraceToLogcat() {
    const size_t maxStackDeep = 40;
    intptr_t stackBuf[maxStackDeep];
    char outBuf[2048];
    memset(outBuf, 0, sizeof(outBuf));
    dumpBacktraceIndex(outBuf, stackBuf, captureBacktrace(stackBuf, maxStackDeep));
    ALOGE(" %s\n", outBuf);
}

/**
 * 0 all
 * 1 net fd
 * 2 whenhook
 */
int logLevelv = 1;

struct addr {
    char ip[128];
    int port;
};

struct addr parse(const struct sockaddr *__addr) {
    struct addr t;
    if (__addr == NULL) {
        return t;
    }
    if (__addr->sa_family == AF_INET) {
        struct sockaddr_in *sa4 = (struct sockaddr_in *) __addr;
        inet_ntop(AF_INET, (void *) (struct sockaddr *) &sa4->sin_addr, t.ip, 128);
        t.port = ntohs(sa4->sin_port);
    } else if (__addr->sa_family == AF_INET6) {
        struct sockaddr_in6 *sa6 = (struct sockaddr_in6 *) __addr;
        char *ipv6 = NULL;
        inet_ntop(AF_INET6, (void *) (struct sockaddr *) &sa6->sin6_addr, t.ip, 128);
        ipv6 = strstr(t.ip, "f:");
        if (NULL != ipv6) {
            strcpy(t.ip, ipv6 + 2);
        }
        t.port = ntohs(sa6->sin6_port);
    } else {
    }
    return t;

}

int protectedFd[1000];

int vpnFd;

int hook_fds[1024];

int isNetFd(int fd) {
    return hook_fds[fd] == AF_INET || hook_fds[fd] == AF_INET6;
}

int needHook(int fd) {
    return vpnFd != 0 && isNetFd(fd) && !protectedFd[fd];
}

void log_me(int fd, const struct sockaddr *__addr, const char *fmt, ...) {
    if (!needHook(fd) && logLevelv == 2) {
        return;
    }
    if (!isNetFd(fd) && logLevelv == 1) {
        return;
    }
    va_list ap;
    char buf[100];
    va_start(ap, fmt);
    vsnprintf(buf, sizeof(buf), fmt, ap);
    va_end(ap);
    char addrs[100];
    if (__addr != NULL) {
        struct addr t = parse(__addr);
        sprintf(addrs, "addr family:%d host=%s:%d", __addr->sa_family, t.ip, t.port);
    }
    ALOGE("iohook fd: %d %s %s",
          fd, buf, addrs);

}

#define logfd(f, t, ...) log_me(f,NULL,t,__VA_ARGS__);
//int socket(int __af, int __type, int __protocol);
HOOK_DEF(int, socket, int __af, int __type, int __protocol) {
    int ret = syscall(__NR_socket, __af, __type, __protocol);
    if (__af > PF_LOCAL) {
//        ALOGE("IOH socket new fd:%d %d %d %d", ret, __af, __type, __protocol);
        hook_fds[ret] = __af;
    }
    return ret;
}

//int connect(int __fd, const struct sockaddr* __addr, socklen_t __addr_length)
HOOK_DEF(int, connect, int __fd, const struct sockaddr *__addr, socklen_t __addr_length) {
//    log_me(__fd, __addr, "connect");
    if (needHook(__fd)) {
        return syscall(__NR_connect, vpnFd, __addr, __addr_length);
    }
    int ret = syscall(__NR_connect, __fd, __addr, __addr_length);
    return ret;
}

char *parsebuf(const void *__buf, size_t __n) {
    const char *buf = (const char *) __buf;
    char *b = malloc(__n * 2 + 1);
    for (int i = 0; i < __n; i++)
        sprintf(b + i * 2, "%02x", buf[i]);
    return b;
}
//ssize_t send(int __fd, const void* __buf, size_t __n, int __flags)
HOOK_DEF(size_t, send, int __fd, const void *__buf, size_t __n, int __flags) {
//    backtraceToLogcat();
    char *buf_str = parsebuf(__buf, __n);
//    if (memcmp(buf_str, "0000", 4) == 0) {
//        char s = *(char *) 0xdeadbeef;
//    };
    logfd(__fd, "send %s", buf_str);
    free(buf_str);
    if (needHook(__fd)) {
        orig_send(vpnFd, __buf, __n, __flags);
    }
    return orig_send(__fd, __buf, __n, __flags);
}

//ssize_t recv(int __fd, void* __buf, size_t __n, int __flags)
HOOK_DEF(ssize_t, recv, int __fd, void *__buf, size_t __n, int __flags) {
//    logfd(__fd, "recv %s", __buf);
    if (needHook(__fd)) {
        orig_recv(vpnFd, __buf, __n, __flags);
    }
    return orig_recv(__fd, __buf, __n, __flags);
}

toString(const void *__buf) {
    size_t a = sizeof(__buf);

}
//ssize_t sendto(int __fd, const void* __buf, size_t __n, int __flags, const struct sockaddr* __dst_addr, socklen_t __dst_addr_length)
HOOK_DEF(ssize_t, sendto, int __fd, const void *__buf, size_t __n, int __flags, const struct sockaddr *__dst_addr,
         socklen_t __dst_addr_length) {
    log_me(__fd, __dst_addr, "sendto %s", __buf);
    if (needHook(__fd)) {
        return orig_sendto(vpnFd, __buf, __n, __flags, __dst_addr, __dst_addr_length);
    }
    ssize_t ret = orig_sendto(__fd, __buf, __n, __flags, __dst_addr, __dst_addr_length);
    return ret;
}
// ssize_t recvmsg(int __fd, struct msghdr* __msg, int __flags)
HOOK_DEF(ssize_t, recvmsg, int __fd, struct msghdr *__msg, int __flags) {
//    logfd(__fd, "recvmsg %s", __msg);
    if (needHook(__fd)) {
        orig_recvmsg(vpnFd, __msg, __flags);
    }
    return orig_recvmsg(__fd, __msg, __flags);
}

//ssize_t sendmsg(int __fd, const void* __buf, size_t __n, int __flags, const struct sockaddr* __dst_addr, socklen_t __dst_addr_length)
HOOK_DEF(ssize_t, sendmsg, int __fd, const struct msghdr *__msg, int __flags) {
    logfd(__fd, "sendmsg %s", __msg);
    if (needHook(__fd)) {
        return orig_sendmsg(vpnFd, __msg, __flags);
    }
    ssize_t ret = orig_sendmsg(__fd, __msg, __flags);
    return ret;
}

//ssize_t recvfrom(int __fd, void* __buf, size_t __n, int __flags, struct sockaddr* __src_addr, socklen_t* __src_addr_length)
HOOK_DEF(ssize_t, recvfrom, int __fd, void *__buf, size_t __n, int __flags, struct sockaddr *__src_addr,
         socklen_t *__src_addr_length) {
//    log_me(__fd, __src_addr, "recvfrom %s", __buf);
    if (needHook(__fd)) {
        return orig_recvfrom(vpnFd, __buf, __n, __flags, __src_addr, __src_addr_length);
    }
    ssize_t ret = orig_recvfrom(__fd, __buf, __n, __flags, __src_addr, __src_addr_length);
    return ret;
}

//int close(int __fd);
HOOK_DEF(int, close, int __fd) {
//    logfd(__fd, "close");
    int ret = syscall(__NR_close, __fd);
    return ret;
}

//ssize_t read(int __fd, void* __buf, size_t __count)
HOOK_DEF(ssize_t, read, int __fd, void *__buf, size_t __count) {
//    logfd(__fd, "read %s", __buf);
    if (needHook(__fd)) {
        return orig_read(vpnFd, __buf, __count);
    }
    return orig_read(__fd, __buf, __count);
}

//ssize_t write(int __fd, const void* __buf, size_t __count)
HOOK_DEF(ssize_t, write, int __fd, const void *__buf, size_t __count) {
    logfd(__fd, "write %s", __buf);
    if (needHook(__fd)) {
        return orig_write(vpnFd, __buf, __count);
    }
    return orig_write(__fd, __buf, __count);
}
//ssize_t readv(int __fd, const struct iovec* __iov, int __count);
HOOK_DEF(ssize_t, readv, int __fd, const struct iovec *__iov, int __count) {
//    logfd(__fd, "readv");
    if (needHook(__fd)) {
        return orig_readv(vpnFd, __iov, __count);
    }
    return orig_readv(__fd, __iov, __count);
}
//ssize_t writev(int __fd, const struct iovec* __iov, int __count)
HOOK_DEF(ssize_t, writev, int __fd, const struct iovec *__iov, int __count) {
    logfd(__fd, "writev", NULL);
    if (needHook(__fd)) {
        return orig_writev(vpnFd, __iov, __count);
    }
    return orig_writev(__fd, __iov, __count);
}

//struct hostent *gethostbyname(const char *__name);
HOOK_DEF(struct hostent, gethostbyname, const char *__name) {
    ALOGE("gethostbyname %s", __name);
    return orig_gethostbyname(__name);
}

//int getaddrinfo(const char* __node, const char* __service, const struct addrinfo* __hints, struct addrinfo** __result);
HOOK_DEF(int, getaddrinfo, const char *__node, const char *__service, const struct addrinfo *__hints,
         struct addrinfo **__result) {
    return orig_getaddrinfo(__node, __service, __hints, __result);
}
//void freeaddrinfo(struct addrinfo* __ptr);
HOOK_DEF(void, freeaddrinfo, struct addrinfo *__ptr) {
    orig_freeaddrinfo(__ptr);
}

//struct hostent* gethostbyaddr(const void* __addr, socklen_t __length, int __type);
HOOK_DEF(struct hostent*, gethostbyaddr, const void *__addr, socklen_t __length, int __type) {
    return orig_gethostbyaddr(__addr, __length, __type);
}

static void do_init(void) {
    //socket hook
    SETUP_SYM(socket);
    SETUP_SYM(connect);
//
    SETUP_SYM(send);// send recv will call sendto or recvfrom
    SETUP_SYM(recv);
//
    SETUP_SYM(recvmsg);
    SETUP_SYM(sendmsg);

    SETUP_SYM(recvfrom);
    SETUP_SYM(sendto);
    //fd
    SETUP_SYM(close);
    SETUP_SYM(read);
    SETUP_SYM(write);

    SETUP_SYM(readv);
    SETUP_SYM(writev);

    SETUP_SYM(gethostbyname);
    SETUP_SYM(getaddrinfo);
//    SETUP_SYM(freeaddrinfo);
    SETUP_SYM(gethostbyaddr);

    /*
     * read()/write() io
        recv()/send()  socket
        readv()/writev() unknow
        recvmsg()/sendmsg() socket
        recvfrom()/sendto() socket
     */
}

static void init_lib_wrapper(const char *caller) {
    pthread_once(&init_once, do_init);
}

#if __GNUC__ > 2

__attribute__((constructor))
static void gcc_init(void) {
    INIT();
}

#endif