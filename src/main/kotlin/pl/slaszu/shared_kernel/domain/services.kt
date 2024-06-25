package pl.slaszu.shared_kernel.domain

interface EventDispatcher {
    fun dispatch(event: Any)
}