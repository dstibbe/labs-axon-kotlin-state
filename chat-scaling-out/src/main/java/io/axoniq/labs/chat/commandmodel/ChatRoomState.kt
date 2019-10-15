package io.axoniq.labs.chat.commandmodel

import io.axoniq.labs.chat.coreapi.*

data class ChatRoomState(
        val participants: Set<String> = emptySet()
) : AggregateState<ChatEvent, ChatCommand> {

    companion object {
        fun createBy(event: RoomCreatedEvent) = ChatRoomState()
    }

    override fun after(event: ChatEvent): ChatRoomState {
        return when (event) {
            is ParticipantJoinedRoomEvent -> this.copy(
                    participants = this.participants + event.participant
            )
            is ParticipantLeftRoomEvent -> this.copy(
                    participants = this.participants - event.participant
            )
            else -> this
        }
    }
}

fun ChatRoomState.evaluate(command: ChatCommand): ChatEvent? {
    return when (command) {
        is JoinRoomCommand -> when {
            !participants.contains(command.participant) -> ParticipantJoinedRoomEvent(command.participant, command.roomId)
            else -> throw AlreadyJoined()
        }
        is LeaveRoomCommand -> when {
            participants.contains(command.participant) -> ParticipantLeftRoomEvent(command.participant, command.roomId)
            else -> null
        }
        is PostMessageCommand -> when {
            participants.contains(command.participant) -> MessagePostedEvent(command.participant, command.roomId, command.message)
            else -> throw NotJoined()
        }
        else -> null
    }
}


