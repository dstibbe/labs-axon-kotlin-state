package io.axoniq.labs.chat.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier

interface ChatCommand
data class CreateRoomCommand(@TargetAggregateIdentifier val roomId: String, val name: String) : ChatCommand
data class JoinRoomCommand(val participant: String, @TargetAggregateIdentifier val roomId: String) : ChatCommand
data class PostMessageCommand(
        val participant: String,
        @TargetAggregateIdentifier val roomId: String,
        val message: String
) : ChatCommand

data class LeaveRoomCommand(val participant: String, @TargetAggregateIdentifier val roomId: String) : ChatCommand

interface ChatEvent
data class RoomCreatedEvent(val roomId: String, val name: String) : ChatEvent
data class ParticipantJoinedRoomEvent(val participant: String, val roomId: String) : ChatEvent
data class MessagePostedEvent(val participant: String, val roomId: String, val message: String) : ChatEvent
data class ParticipantLeftRoomEvent(val participant: String, val roomId: String) : ChatEvent

sealed class ChatCommandException(msg:String? = null) : RuntimeException(msg)
class AlreadyJoined : ChatCommandException()
class NotJoined : ChatCommandException()


class AllRoomsQuery
data class RoomParticipantsQuery(val roomId: String)
data class RoomMessagesQuery(val roomId: String)
