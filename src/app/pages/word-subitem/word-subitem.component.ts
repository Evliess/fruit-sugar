import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { ActivatedRoute } from '@angular/router';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { SugarDictService } from '../../services/sugar-dict';
import { Router } from '@angular/router';


@Component({
  selector: 'app-word-subitem',
  imports: [CommonModule, NzGridModule, NzCardModule, NzButtonModule],
  templateUrl: './word-subitem.component.html',
  styleUrl: './word-subitem.component.css'
})
export class WordSubitemComponent {
  wordSubitems: any[] = []
  isCustom: boolean = false;
  parentName: string = '';
  parentDescription: string = '';
  private router = inject(Router);
  constructor(private route: ActivatedRoute) { }
  private sugarDictService = inject(SugarDictService)
  ngOnInit() {
    this.route.queryParamMap.subscribe(paramMap => {
      this.isCustom = paramMap.get('isCustom') === 'true';
      this.parentName = paramMap.get('parentName') || '';
      this.parentDescription = paramMap.get('parentDescription') || '';
      this.sugarDictService.getChildrenContentModules(this.parentName).subscribe((data: any) => {
        this.wordSubitems = data.children || [];
      });
    });

  }

  gotoWords(moduleId: number) {
    this.router.navigate(['/words'], { queryParams: { isCustom: this.isCustom, moduleId: moduleId } });
  }

  getChineseContentModuleName(name: string) {
    return this.sugarDictService.getChineseContentModuleName(name);
  }
  getEnglishContentModuleName(name: string) {
    return this.sugarDictService.getEnglishContentModuleName(name);
  }

}
