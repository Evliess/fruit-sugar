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

    getChildrenContentModules(parentName: string, userId: number): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/children-content-module?parentName=" + parentName + "&userId=" + userId);
    }

    getAllChildrenContentModules(): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/all-children-content-module");
    }

    resetWordsProgressByChildContentModuleId(childContentModuleId: number, userId: number): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/content-module/reset-learned-count", { moduleId: childContentModuleId, userId: userId });
    }

    getWordsByChildContentModuleId(childContentModuleId: number, userId: number): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/words/by-child-content-module-id?childModuleId=" + childContentModuleId + "&userId=" + userId);
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

    markWordAsKnown(userId: number, wordId: number, moduleId: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/word/mark-as-known", { userId: userId, wordId: wordId, moduleId: moduleId });
    }

    markWordAsUnknown(userId: number, wordId: number, moduleId: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/word/mark-as-unknown", { userId: userId, wordId: wordId, moduleId: moduleId });
    }

    markSentenceAsUnKnown(userId: number, sentenceId: number, moduleId: string): Observable<String> {
        return this.http.post<String>(`${this.apiUrl}` + "/sentence/mark-as-unknown", { userId: userId, sentenceId: sentenceId, moduleId: moduleId });
    }
    
    getUserUnknown(userId: number): Observable<String> {
        return this.http.get<String>(`${this.apiUrl}` + "/user-unknown/user-id/" + userId);
    }
    removeUserUnknownWord(userId: number, wordId: number): Observable<String> {
        return this.http.delete<String>(`${this.apiUrl}` + "/user-unknown-word/"+ userId + "/" + wordId);
    }

    removeUserUnknownSentence(userId: number, sentenceId: number): Observable<String> {
        return this.http.delete<String>(`${this.apiUrl}` + "/user-unknown-sentence/"+ userId + "/" + sentenceId);
    }

}