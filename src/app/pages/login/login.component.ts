import { Component, inject } from '@angular/core';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [NzFormModule, NzInputModule, NzButtonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  login() {
    this.authService.setSession({ id: '1', name: 'Test User', role: 'user' });
    this.router.navigate(['/home']);
  }

}
