interface UserConnectionEvent {
  email :string,
  nickname: string,
  type :string
}

interface ChatMessage {
  from :string
  datetime :Date
  content: string
}
