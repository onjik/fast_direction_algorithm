# Fast Travel Direction Search Demo
> Hierarchical Clustering 을 활용한 Traveling Salesman Problem (TSP) 해결 

[Managed Travel Service](https://github.com/onjik/Managed-Travel-Service) 프로젝트에서 사용자가 선택한 여행지에 대해 근사 최적 경로를 제공하기 위한 알고리즘 데모

## Result
다음은 10개의 랜덤 포인트에 대한 경로 생성을 시각화 한 것이다.

일단 Hierarchical Clustering 을 통해 클러스터링 트리를 만든다.
![cluster_log.png](docs/cluster_log.png)

이 트리를 활용하여, 근사 경로를 만든다.
![visualization1.png](docs/visualization1.png)
Swing 을 사용해서 간단하게 시각화만 하였다. 저 아래 버튼을 누르면 새로운 케이스를 보여준다.
![img.png](docs/visualization2.png)
![img_1.png](docs/visualization3.png)
![img_2.png](docs/visualization4.png)

## Execution Time
![](docs/일반_대.png)
![](docs/로그_대.png)
![](docs/일반_소.png)
![](docs/로그_소.png)

## Details
TSP(Traveling Salesman Problem, 외판원 문제) 를 해결하기 위해, 기존에 많이 사용하는 단순 순회나, 근사치를 구하기 위한 유전 알고리즘, 담금질 기법을 많이 사용한다.

나의 경우, 프로젝트 특성상 다음과 같은 요구사항이 필요했다.
- 경로가 고정된 것이 아닌, 사용자가 실시간으로 경로를 추가한다.
- 근사치가 허용된다. 
- 대신 속도가 느리면 안된다.
- 사용자의 경로를 우선 DB Scan 이나 K-Means 로 날짜 별로 분할 한 후 사용할 것이기 때문에, 예상되는 N은 2개 ~ 50개 범위가 가장 빈번할 것이다.

로직은 다음과 같은 개요를 가진다.
1. Hierarchical Clustering 을 사용하여 클러스터링 트리를 만든다
2. 이 트리를 활용하여, 근사 경로를 만든다. 
3. 꼬인 경로를 후처리한다.

이렇게 한다면, 경로가 생략되거나 추가되더라도, 트리를 통해 더 효율적으로 처리할 수 있기 때문에, 매번 새로운 알고리즘을 돌리는 것 보다 효율적이라고 생각하였다.

## 추후 개선 사항
- 접히는 현상이 고질적으로 관찰된다.
![](docs/fold1.png)
![](docs/fold2.png)
- 최적 경로를 해치지 않으면서 트리에 추가하고 삭제하는 방식을 고민해야한다. 