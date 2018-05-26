#ifndef DEBUG_H
#define DEBUG_H

#ifdef DEBUG

# include <stdio.h>
# define PSTDERR(fmt, args...) do { dprintf(2,fmt, ## args); } while(0)
# define PDEBUG(fmt, args...) PSTDERR("DEBUG:"fmt, ## args)
# define DEBUGDECL(args...) args

# include "core.h"
void DUMP_PROXY_CHAIN(proxy_data *pchain, unsigned int count);

#else

#include <android/log.h>

# define PDEBUG(fmt, args...) do {__android_log_print(ANDROID_LOG_DEBUG,"legency",fmt, ## args);} while (0)
# define DEBUGDECL(args...)
# define DUMP_PROXY_CHAIN(args...) do {} while (0)
#endif

# define PFUNC() do { PDEBUG("pid[%d]:%s\n", getpid(), __FUNCTION__); } while(0)

#endif

