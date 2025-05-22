const storagePrefix = 'waveguide_';

export const storage = {
  getToken: () => {
    return JSON.parse(window.localStorage.getItem(`${storagePrefix}token`) || 'null');
  },
  setToken: (token: string) => {
    window.localStorage.setItem(`${storagePrefix}token`, JSON.stringify(token));
  },
  clearToken: () => {
    window.localStorage.removeItem(`${storagePrefix}token`);
  },
  getUser: () => {
    return JSON.parse(window.localStorage.getItem(`${storagePrefix}user`) || 'null');
  },
  setUser: (user: unknown) => {
    window.localStorage.setItem(`${storagePrefix}user`, JSON.stringify(user));
  },
  clearUser: () => {
    window.localStorage.removeItem(`${storagePrefix}user`);
  },
};