import { Component } from '@angular/core';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { RouterModule } from '@angular/router';



@Component({
  selector: 'app-shopping',
  imports: [NzGridModule, NzCardModule,RouterModule],
  templateUrl: './shopping.component.html',
  styleUrl: './shopping.component.css'
})
export class ShoppingComponent {

}
