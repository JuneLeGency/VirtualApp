package com.lc.puppet.storage;

import com.lc.puppet.storage.transfers.BaseServerTransfer;
import com.lc.puppet.storage.transfers.ClientTransfer;
import com.lc.puppet.storage.transfers.ServerTransfer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author legency
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IObType {
    Class<?> value();

    Class<? extends ServerTransfer> serverTransfer() default BaseServerTransfer.class;

    Class<? extends ClientTransfer> clientTransfer() default ClientTransfer.class;

}
