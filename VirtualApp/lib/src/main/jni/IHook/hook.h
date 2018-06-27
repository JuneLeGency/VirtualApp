//
// Created by legency on 2018/5/29.
//

#ifndef PROXYCHAINSNG_HOOK_H
#define PROXYCHAINSNG_HOOK_H

void i_hook_inner(void *symbol, void *replace, void **result);
#ifdef USE_XHOOK
#define i_hook(a,b,c) i_hook_inner(#a,b,c)
#else
#define i_hook(a,b,c) i_hook_inner(a,b,c)
#endif
void i_hook_done();
#endif //PROXYCHAINSNG_HOOK_H
