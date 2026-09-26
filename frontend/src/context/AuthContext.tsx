import {
  createContext,
  useContext,
  useState,
  type ReactNode,
} from "react";

export type UserRole = "CUSTOMER" | "RESTAURANT" | "ADMIN";

export interface AuthUser {
  id: string;
  name: string;
  email: string;
  role: UserRole;
}

interface AuthContextType {
  user: AuthUser | null;
  login: (email: string, password: string) => boolean;
  register: (name: string, email: string, password: string) => boolean;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(
  undefined,
);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const storedUser = localStorage.getItem("foodflow-user");

    return storedUser ? JSON.parse(storedUser) : null;
  });

  const login = (email: string, password: string) => {
    if (!email || !password) {
      return false;
    }

    const storedUser = localStorage.getItem("foodflow-user");

    if (storedUser) {
      const existingUser = JSON.parse(storedUser);

      if (existingUser.email === email) {
        setUser(existingUser);
        return true;
      }
    }

    const newUser: AuthUser = {
      id: `user-${Date.now()}`,
      name: email.split("@")[0],
      email,
      role: "CUSTOMER",
    };

    localStorage.setItem(
      "foodflow-user",
      JSON.stringify(newUser),
    );

    setUser(newUser);

    return true;
  };

  const register = (
    name: string,
    email: string,
    password: string,
  ) => {
    if (!name || !email || !password) {
      return false;
    }

    const newUser: AuthUser = {
      id: `user-${Date.now()}`,
      name,
      email,
      role: "CUSTOMER",
    };

    localStorage.setItem(
      "foodflow-user",
      JSON.stringify(newUser),
    );

    setUser(newUser);

    return true;
  };

  const logout = () => {
    localStorage.removeItem("foodflow-user");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error(
      "useAuth must be used inside AuthProvider",
    );
  }

  return context;
}

