import { Component, inject } from '@angular/core';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { AuthService } from '../../services/auth';
import { SugarDictService } from '../../services/sugar-dict';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [NzFormModule, NzInputModule, NzButtonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private authService = inject(AuthService);
  private sugarDictService = inject(SugarDictService);
  private router = inject(Router);
  token: string = '';

  login() {
    this.sugarDictService.userLogin(this.token).subscribe({
      next: (response: any) => {
        if (response && response.result === 'ok') {
          this.authService.setSession({ id: response.id, code: response.token, name: response.user, role: 'user' });
          this.router.navigate(['/home']);
        } else {
          this.router.navigate(['/login']);
          alert('Login failed. Please check your token and try again.');
        }
      },
      error: () => {
        this.router.navigate(['/login']);
        alert('Login failed. Please check your token and try again.');
      }
    });
  }

  inputToken($event: any) {
    this.token = $event.target.value;
  }

}
