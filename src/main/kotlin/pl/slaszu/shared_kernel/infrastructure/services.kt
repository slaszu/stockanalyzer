package pl.slaszu.shared_kernel.infrastructure

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import pl.slaszu.shared_kernel.domain.EventDispatcher

@Service
class SpringEventDispatcher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventDispatcher {
    override fun dispatch(event: Any) {
        this.applicationEventPublisher.publishEvent(event)
    }
}