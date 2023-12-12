# devcode
개발 프로젝트 진행시 필요한 기능을 구현한 프로젝트

## 모듈별 정보
### devcode-web
* devcode의 전반적인 정보 및 상황을 확인할 수 있는 웹 모듈

### devcode-cmd
* 콘솔에서 명령어를 실행할 수 있는 모듈

### devcode-collector
* 정보를 수집하는 수집기
* 목표
  - OS의 기존정보 및 리소스 정보수집
  - Log4j2의 TCP/UDP 정보수집

### devcode-agent-shell
* 리눅스에서 실행되는 쉘스크립트 기반의 agent
* 목표
  - 리눅스의 기본정보 및 리소스 정보를 쉘스크립트 기반으로 가볍게 실행되는 에이전트로 개발
  - curl 명령어로 collector 실행