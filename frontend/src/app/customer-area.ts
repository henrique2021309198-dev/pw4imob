import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  imports: [RouterLink],
  templateUrl: './customer-area.html',
})
export class CustomerArea {
  protected readonly auth = inject(AuthService);
}
