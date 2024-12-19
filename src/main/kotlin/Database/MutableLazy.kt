package com.beauver.discord.bots.Database

class MutableLazy<T>(private var initializer: () -> T) {
    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): T {
        if (value == null) {
            value = initializer()
        }
        return value!!
    }

    operator fun setValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>, newValue: T) {
        value = newValue
    }
}