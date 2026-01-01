import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SugarDictService {
    private http = inject(HttpClient);

    public apiUrl = "http://localhost:8080/sugar-dict/public/v1";

    getAllContentModules(): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/home/all-modules");
    }

    getChildrenContentModules(parentName: string): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/children-content-module?parentName=" + parentName);
    }

    getAllChildrenContentModules(): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/all-children-content-module");
    }

    getWordsByChildContentModuleId(childContentModuleId: number): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/words/by-child-content-module-id?childModuleId=" + childContentModuleId);
    }

    getWordsSimpleByChildContentModuleId(childContentModuleId: number): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/words/simple/by-child-content-module-id?childModuleId=" + childContentModuleId);
    }

    getSentencesByContentModuleId(contentModuleId: number): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/sentences/by-content-module-id?moduleId=" + contentModuleId);
    }

    getTextDigest(text: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/text/digest", { text: text });
    }

    getTextTts(text: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/text/tts", { text: text });
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

    userLogin(code: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/user/login", { code: code });
    }
    adminLogin(name: string, code: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/user/admin-login", { name: name, code: code });
    }
    createUser(name: string, days: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/user/create-or-update", { name: name, days: days });
    }
    revokeUser(code: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/user/revoke-token", { code: code });
    }

}