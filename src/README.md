# APD - Tema 2 - Planificarea taskurilor intr-un datacenter
## Arghir Monica-Andreea 332CA

Pentru a implementa dispatcher-ul, am verificat ce fel de algortim urmeaza,
astfel incat sa pot implementa logica fiecaruia.

# Round Robin:
Pentru RR, mi-am creat o variabila in care am retinut id-ul fiecarui host
pentru a putea calcula pe baza formulei "(id_of_last_host + 1) % numarul_de_host-uri".
# Size Interval Task Assignment
Pentru SITA, am verificat la fiecare task ce tip are, pentru a trimite mai departe catre host-uri.
# Shortest Queue
Aici a trebuit sa calculez pentru fiecare host, lungimea cozii acestuia, astfel incat
task-ul sa se adauge in host-ul cu lungimea cozii cea mai mica.
Pentru a face asta, am trecut prin fiecare host, pentru a cauta minimul. In cazul 
in care 2 host-uri ar avea acelasi numar de elemente in cozile lor, setam index-ul corect dupa
verificarea id-ului cel mai mic dintre cele 2 host-uri.
# Least Work Left
Procesul de adaugare a task-urilor se face in fel asemenator cu cel de la SQ.

## Implementarea MyHost

Pentru a reusi sa pastrez task-urile fiecarui host in ordinea corecta am ales sa folosesc o coada de prioritati
blocanta astfel incat sa evit racing condition. Coada va compara prioritatile sau timpul de start al task-urilor.
Am pastrat un task in evidenta ca fiind cel care ruleaza in momentul respectiv.
O variabila pentru a stii atunci cand host-ul se opreste si doua variabile pentru a tine minte timpul la care
task-ul curent intra in executie si timpul cand isi opreste executia, daca este intrerupt de aparitia unui task cu prioritate mai mare.

Atunci cand se adauga un task in coada se verifica daca task-ul nou adaugat are o prioritate mai mare, dar
pentru a intrerupe executia task-ului curent, verificam si ca acesta sa fie preemptiv.

Primul element din coada se considera cel care trebuie executat, astfel ca, daca coada nu este goala, 
vom lua primul element in currTask. Punem programul sa astepte pentru durata task-ului si pastram timpul in
care acel task a intrat in executie. Daca functia sleep nu este intrerupta de aparitia unui task mai bun,
task-ul termina si este setat pe null.

In schimb daca executia sleep este intrerupta trebuie sa calculam durata pe care task-ul a apucat sa o execute,
ca sa aflam cat timp mai are. Daca ajunge sa fie 0, inseamna ca task-ul s-a terminat, dar daca nu, este adaugat inapoi in coada
pentru a reveni la executie, dupa ce task-ul prioritar termina.

Pentru functia ce afla numarul task-urilor existente in coada unui host, am adaugat un element in plus la dimensiunea cozii,
atunci cand un task este in momentul respectiv rulat, deoarece atunci cand execut un task, el este scos din coada prin metoda take().

Pentru functia care calculeaza timpul pe care intregul host il mai are de executat, parcurg
task-urile existente in coada si le adun timpul, iar daca avem si un task care ruleaza, adun cat timp acesta mai are de executat (scad cat timp
a reusit sa stea deja).
