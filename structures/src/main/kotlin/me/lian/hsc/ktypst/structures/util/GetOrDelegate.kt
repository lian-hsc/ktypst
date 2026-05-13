package me.lian.hsc.ktypst.structures.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible

class GetOrDelegate<T>(val actual: KMutableProperty<T?>, val delegate: KProperty<T>?) : ReadWriteProperty<Any?, T> {

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        actual.setter.also { if (!it.isAccessible) it.isAccessible = true }.call(value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val actualValue = actual.getter.also { if (!it.isAccessible) it.isAccessible = true }.call()
        if (actualValue != null) return actualValue

        return delegate
            ?.getter
            ?.also { if (!it.isAccessible) it.isAccessible = true }
            ?.call()
            ?: error("Can not fetch value from null delegate.")
    }

}
