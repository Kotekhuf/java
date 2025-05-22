import { createContext, useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { LoginRequest, UserRegistrationRequest } from '@/types';
import { axios } from '@/lib/axios';
import { storage } from '@/utils/storage';

interface AuthContextType {
  user: any | null;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: UserRegistrationRequest) => Promise<void>;
  logout: () => Promise<void>;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<any | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = storage.getToken();
    if (token) {
      const userData = storage.getUser();
      setUser(userData);
    }
    setIsLoading(false);
  }, []);

  const login = async (data: LoginRequest) => {
    const response = await axios.post('/api/v1/auth/login', data);
    const { token, ...user } = response.data;
    storage.setToken(token);
    storage.setUser(user);
    setUser(user);
    navigate('/');
  };

  const register = async (data: UserRegistrationRequest) => {
    const response = await axios.post('/api/v1/auth/register', data);
    const { token, ...user } = response.data;
    storage.setToken(token);
    storage.setUser(user);
    setUser(user);
    navigate('/');
  };

  const logout = async () => {
    const token = storage.getToken();
    if (token) {
      await axios.post('/api/v1/auth/logout');
    }
    storage.clearToken();
    storage.clearUser();
    setUser(null);
    navigate('/login');
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}