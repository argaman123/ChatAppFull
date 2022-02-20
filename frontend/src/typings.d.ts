interface UserConnectionEvent {
  email :string,
  nickname: string,
  type :string
}

interface PremiumStatus {
  expiration?: Date,
  plan: string
}

interface ChatMessage {
  from :string
  datetime :Date
  content: string
  type: string
}
