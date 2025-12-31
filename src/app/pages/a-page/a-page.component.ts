import { Component, inject } from '@angular/core';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { SugarDictService } from '../../services/sugar-dict';

@Component({
  selector: 'app-a-page',
  imports: [NzFormModule, NzInputModule, NzButtonModule],
  templateUrl: './a-page.component.html',
  styleUrl: './a-page.component.css'
})
export class APageComponent {
  name: string = '';
  days: string = '';
  token: string = '';

  private sugarDictService = inject(SugarDictService);

  constructor(private notification: NzNotificationService) { }

  generate() {
    if (this.name.trim() !== '' && this.isPureNumber(this.days)) {
      this.sugarDictService.createUser(this.name, this.days).subscribe({
        next: (response: any) => {
          if (response && response.result === 'ok') {
            this.notification.create(
              'success',
              this.name,
              `口令： ${response.token}`,
              { nzDuration: 0 }
            );
          } else {
            alert('Failed to create/update user. Please try again.');
          }
        },
        error: () => {
          alert('Failed to create/update user. Please try again.');
        }
      });
    }
  }

  inputName($event: any) {
    if ($event && $event.target.value !== undefined) {
      this.name = $event.target.value;
    }
  }

  isPureNumber(value: string | number): boolean {
    return /^\d+$/.test(value.toString());
  }

  inputDays($event: any) {
    if ($event && $event.target.value !== undefined) {
      if (this.isPureNumber($event.target.value)) {
        this.days = $event.target.value;
      }
    }
  }

}
