# Projekt-JwZP

**Wymagania projektowe:
■ Domenę i zakres funkcjonalny należy ustalić z prowadzącym\
■ Kod źródłowy aplikacji należy utrzymywać na Githubie lub Gitlabie\
■ Kod powinien być automatycznie budowany (gradle lub maven), a wynik
budowania (pass/fail) powinien być widoczny w repozytorium.\
■ Kod powinien być automatycznie testowany, przynajmniej na dwóch
poziomach: jednostkowo i poprzez interfejs HTTP.\
○ Testowanie powinno być elementem budowania projektu.\
○ Wyniki testowania powinny być widoczne w repozytorium.\
■ Aplikację należy przetestować wydajnościowo z użyciem np Gatlinga.
Nie powinien to być element budowania automatycznego, ale powinno być
możliwe uruchomienie takiego testu jedną komendą.\
■ Kod powinien być automatycznie skanowany pod kątem bezpieczeństwa (np
Snyk).\
■ Dokumentację API należy udostępnić w formie Swaggera.\
■ Usługa powinna udostępniać poprawnie skonstruowane REST API, łącznie z
obsługą błędów.\
■ Musi być wykorzystywana data i czas, z poprawnym użyciem klasy Clock.
Funkcjonalność powinna obejmować strefy czasowe, które należy poprawnie
obsłużyć.\
■ Dependency Injection: należy używać wstrzykiwania przez konstruktor\
■ Należy używać bibliotekę do logów.\
■ Proces budowania powinien wytwarzać gotowy do wdrożenia artefakt: plik jar.\
■ Należy dostarczyć pliki: Dockerfile oraz Docker Compose, zawierające
kompletne środowisko uruchomieniowe.\
■ Usługa powinna zawierać persystencję. Baza danych w odrębnym Dockerfile,
połączona poprzez compose.\
■ Usługa powinna być monitorowana jakimś środowiskiem monitorującym, np
ELK. Środowisko to powinno być dostarczone w odrębnym Dockerfile,
połączone poprzez compose z pozostałymi komponentami.\
■ Usługa powinna korzystać z jakiegoś zewnętrznego, ogólnodostępnego
źródła danych w Internecie. Przykłady: OpenWheatherMap, JokeAPI, NASA
API, CoinGecko API, InPost API.\
■ Usługa powinna być zabezpieczona przynajmniej na podstawowym poziomie
(np logowanie basic auth, lub z użyciem autoryzacji Google)\
■ Usługa zawierać minimum 3 poziomy uprawnień użytkowników, z czego jeden
(user-admin) powinien służyć do zarządzania użytkownikami.\
■ Użytkownicy powinni mieć ograniczony dostęp do niektórych zasobów (np
tylko swoich).\
■ Należy przygotować demo serwisu, z użyciem np Postmana (i innych
narzędzi), które zademonstrują działanie i monitorowanie systemu.\
■ Wszelkie dodatkowe elementy ustalone z prowadzącym są również
obowiązkowe.**
