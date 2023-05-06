package com.danilkha.composeapptemplate.utils

import kotlin.Lazy

class LazyWithKey<T>(val key: () -> Any?, val init: () -> T): Lazy<T> {
    private var _value: T? = null
    private var lastKey: Any? = null

    override val value: T
        get() {
            if(_value == null){
                lastKey = key()
                _value = init()
            }else{
                if(lastKey != key()){
                    _value = init()
                }
            }
            return _value!!
        }

    override fun isInitialized(): Boolean = _value != null
}

fun <T> lazyKeyed(key: () -> Any?, init: () -> T): Lazy<T> = LazyWithKey(key, init)