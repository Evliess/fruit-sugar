import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SugarDictService {
    private http = inject(HttpClient);

    private apiUrl = "http://localhost:8080/sugar-dict/public/v1";

    getAllContentModules(): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/home/all-modules");
    }

    getChildrenContentModules(parentName: string): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/children-content-module?parentName=" + parentName);
    }

    getChineseContentModuleName(name: string) {
        if (!name.includes("(")) {
            return name;
        }
        return name.split("(")[0];
    }
    getEnglishContentModuleName(name: string) {
        if (!name.includes("(")) {
            return name;
        }
        return name.split("(")[1].slice(0, -1);
    }


}