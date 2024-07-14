import { useEffect, useState } from "react"
import usersData from "../../../../database/json/User.json"
import { User } from "../utils/types"

export default function useUserByLogin(
  username: string | null,
  password: string | null
): User | null {
  const [data, setData] = useState<User | null>(null)

  useEffect(
    function fetchUser() {
      if (!username || !password) {
        setData(null)
      }
      const user = usersData.find(
        (u) => u.username === username && u.password === password
      )
      if (user) {
        setData({
          idUser: user.id_user,
          name: user.name,
          username: user.username,
          email: user.email,
          password: user.password,
        })
      }
      setData(null)
    },
    [username, password]
  )

  return data
}
