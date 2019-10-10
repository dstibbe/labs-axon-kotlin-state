package io.axoniq.labs.chat.commandmodel

import io.axoniq.labs.chat.coreapi.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class ChatRoom {
    @AggregateIdentifier
    private lateinit var roomId: String

    private lateinit var state: ChatRoomState

    constructor()

    @CommandHandler
    constructor(command: CreateRoomCommand) {
        apply(RoomCreatedEvent(command.roomId, command.name))
    }

    @CommandHandler
    fun handleCommand(command: JoinRoomCommand) = state.handle(command)

    @CommandHandler
    fun handleCommand(command: PostMessageCommand) = state.handle(command)

    @CommandHandler
    fun handleCommand(command: LeaveRoomCommand) = state.handle(command)

    @EventSourcingHandler
    fun handleEvent(event: ChatEvent) {
        state = when (event) {
            is RoomCreatedEvent -> {
                initializeId(event)
                ChatRoomState.createBy(event)
            }
            else -> state.after(event)
        }
    }

    private fun initializeId(event: RoomCreatedEvent) {
        this.roomId = event.roomId
    }
}
