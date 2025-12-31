import { Component, inject } from '@angular/core';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { AuthService } from '../../services/auth';
import { SugarDictService } from '../../services/sugar-dict';
import { Router } from '@angular/router';

@Component({
  selector: 'app-a-login',
  imports: [NzFormModule, NzInputModule, NzButtonModule],
  templateUrl: './a-login.component.html',
  styleUrl: './a-login.component.css'
})
export class ALoginComponent {
  private authService = inject(AuthService);
  private sugarDictService = inject(SugarDictService);
  private router = inject(Router);
  token: string = '';
  name: string = '';

  login() {
    this.sugarDictService.adminLogin(this.token, this.name).subscribe({
      next: (response: any) => {
        if(response && response.result === 'ok') {
          this.authService.setSession({id: this.token, name: this.name, role: 'admin' } );
          this.router.navigate(['/admin/admin-page']);
        } else {
          this.router.navigate(['/admin-login']);
          alert('Login failed. Please check your token and name, then try again.');
        }
      },
      error: () => {
        this.router.navigate(['/admin-login']);
        alert('Login failed. Please check your token and name, then try again.');
      }
    });
    
  }

  inputToken($event: any) {
    this.token = $event.target.value;
  }

  inputName($event: any) {
    this.name = $event.target.value;
  }
}
