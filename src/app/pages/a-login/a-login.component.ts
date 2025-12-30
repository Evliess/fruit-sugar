import { Component, inject } from '@angular/core';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-a-login',
  imports: [NzFormModule, NzInputModule, NzButtonModule],
  templateUrl: './a-login.component.html',
  styleUrl: './a-login.component.css'
})
export class ALoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
    login() {
    this.authService.setSession({ id: '1', name: 'Test Admin', role: 'admin' });
    this.router.navigate(['/admin/admin-page']);
  }
}
