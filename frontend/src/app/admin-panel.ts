import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  imports: [RouterLink],
  templateUrl: './admin-panel.html',
})
export class AdminPanel {
  protected readonly auth = inject(AuthService);
}
