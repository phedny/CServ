/*******************************************************************************
 * Copyright (c) 2012 Lopexs.
 * All rights reserved.
 ******************************************************************************/
package nl.limesco.cserv.lib.quartz.annotations.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RepeatCount {

    int value();
    
}
