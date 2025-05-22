export interface LoginRequest {
  email: string;
  password: string;
}

export interface UserRegistrationRequest {
  username: string;
  email: string;
  password: string;
}

export interface LayerRequest {
  E: number;
  reEps: number;
  imEps: number;
  d: number;
}

export interface WaveguideRequest {
  nEffMin: number;
  nEffMax: number;
  layers?: LayerRequest[];
}

export interface LayerResponse {
  id: string;
  layerIndex: number;
  E: number;
  reEps: number;
  imEps: number;
  d: number;
}

export interface WaveguideResponse {
  id: string;
  nEffMin: number;
  nEffMax: number;
  layers: LayerResponse[];
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}