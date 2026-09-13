import { InjectionToken } from '@angular/core';

export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', { providedIn: 'root', factory: () => (globalThis as { HACKATUDO_API_URL?: string }).HACKATUDO_API_URL ?? 'https://api.example.invalid/v1' });
