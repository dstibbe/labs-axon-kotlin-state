package io.axoniq.labs.chat.commandmodel

interface AggregateState<E, C> {
    fun after(event: E): AggregateState<E, C>
}



