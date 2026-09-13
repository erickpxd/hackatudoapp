import { Injectable } from '@angular/core';
import { DEMO_AUTH } from './auth.config';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private token: string | null = null;
  readonly demoEnabled = DEMO_AUTH.enabled;
  setToken(value: string): void { this.token = value; }
  accessToken(): string | null { return this.token; }
}
