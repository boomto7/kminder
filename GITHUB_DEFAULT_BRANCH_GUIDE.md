# GitHub 기본 브랜치를 master로 변경하기

## 📍 저장소 정보
- **저장소**: https://github.com/boomto7/kminder
- **현재 로컬 브랜치**: master
- **원격 브랜치**: main (기본), master

## 🔄 변경 방법

### 1. GitHub 웹사이트에서 변경 (권장)

#### 단계별 가이드:

1. **저장소 페이지 접속**
   ```
   https://github.com/boomto7/kminder
   ```

2. **Settings 탭 클릭**
   - 저장소 상단 메뉴에서 "Settings" 클릭
   - (저장소 소유자 또는 관리자 권한 필요)

3. **Branches 메뉴 선택**
   - 왼쪽 사이드바에서 "Branches" 클릭

4. **Default branch 변경**
   - "Default branch" 섹션 찾기
   - 현재: `main` → 변경할 브랜치: `master`
   - 전환 아이콘(⇄) 또는 연필 아이콘 클릭
   - 드롭다운에서 "master" 선택
   - "Update" 버튼 클릭

5. **확인**
   - 경고 대화상자가 나타남
   - "I understand, update the default branch" 클릭
   - 완료!

### 2. 변경 후 확인

#### GitHub에서 확인:
- 저장소 메인 페이지에서 브랜치 드롭다운 확인
- 기본값이 "master"로 표시되어야 함

#### 로컬에서 확인:
```bash
# 원격 정보 업데이트
git remote update origin --prune

# 기본 브랜치 확인
git remote show origin
```

### 3. main 브랜치 삭제 (선택사항)

기본 브랜치를 master로 변경한 후, main 브랜치가 더 이상 필요없다면 삭제할 수 있습니다:

#### GitHub에서 삭제:
1. Settings → Branches
2. "Your branches" 섹션에서 main 브랜치 찾기
3. 삭제 아이콘 클릭

#### 로컬에서 삭제:
```bash
# 로컬 main 브랜치 삭제
git branch -d main

# 원격 main 브랜치 삭제 (GitHub에서 먼저 삭제하는 것을 권장)
git push origin --delete main
```

## 📋 현재 상태

### 로컬 브랜치:
```
  main
* master  ← 현재 작업 중
```

### 원격 브랜치:
```
  remotes/origin/main
  remotes/origin/master
```

### 커밋 상태:
- master 브랜치가 origin/master보다 6개 커밋 앞서 있음
- 아직 푸시하지 않은 커밋: 5개

## 🚀 다음 단계

### 1. GitHub에서 기본 브랜치 변경
위의 가이드를 따라 웹에서 변경

### 2. 변경 사항 푸시
```bash
# master 브랜치의 변경사항을 원격에 푸시
git push origin master
```

### 3. 로컬 설정 업데이트
```bash
# 원격 정보 업데이트
git remote update origin --prune

# 기본 브랜치 추적 설정
git branch --set-upstream-to=origin/master master
```

## ⚠️ 주의사항

### 기본 브랜치 변경 시 영향:
1. **Pull Request**: 새로운 PR의 기본 대상이 master로 변경됨
2. **Clone**: 새로 clone할 때 master 브랜치가 체크아웃됨
3. **CI/CD**: 기본 브랜치를 참조하는 워크플로우가 있다면 영향받을 수 있음

### 권한 필요:
- 저장소 Settings에 접근하려면 **소유자** 또는 **관리자** 권한이 필요합니다
- 권한이 없다면 저장소 소유자에게 요청해야 합니다

## 📚 참고 링크

- [GitHub Docs: 기본 브랜치 변경](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-branches-in-your-repository/changing-the-default-branch)

---

**변경 후 SourceTree에서도 자동으로 업데이트됩니다!** 🎉
