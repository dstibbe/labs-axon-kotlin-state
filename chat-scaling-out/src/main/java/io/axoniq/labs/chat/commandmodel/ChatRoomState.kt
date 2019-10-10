package io.axoniq.labs.chat.commandmodel

import io.axoniq.labs.chat.coreapi.*
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.springframework.util.Assert
import java.util.*

data class ChatRoomState(
        var participants: Set<String> = HashSet()
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

    override fun handle(command: ChatCommand) {
        when (command) {
            is JoinRoomCommand -> if (!participants.contains(command.participant)) {
                apply(ParticipantJoinedRoomEvent(command.participant, command.roomId))
            }
            is LeaveRoomCommand -> if (participants.contains(command.participant)) {
                apply(ParticipantLeftRoomEvent(command.participant, command.roomId))
            }
            is PostMessageCommand -> {
                Assert.state(
                        participants.contains(command.participant),
                        "You cannot post messages unless you've joined the chat room"
                )
                apply(MessagePostedEvent(command.participant, command.roomId, command.message))
            }
        }
    }
}



