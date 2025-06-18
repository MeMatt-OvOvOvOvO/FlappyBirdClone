### **Wstęp**
#### Cel Projektu
Celem projektu było stworzenie gry inspirowanej klasycznym _Flappy Bird_. Zespół postawił sobie za cel odtworzenie oryginalnej rozgrywki, a także dodanie własnych funkcjonalności, takich jak poziomy trudności, system monet oraz możliwość zapisywania wyników graczy. Gra została zaimplementowana w **Javie** z wykorzystaniem frameworka **JavaFX** do interfejsu graficznego.
#### Kluczowe Funkcjonalności
1. **Mechanika gry:**
   - Gra wykorzystuje fizykę, gdzie ptak poddany jest sile grawitacji. Gracz steruje wysokością lotu ptaka poprzez skoki (klasa `Bird` odpowiedzialna za mechanikę ruchu).
   - Przeszkody w postaci rur są generowane dynamicznie i przesuwają się na ekranie od prawej do lewej.

2. **Poziomy trudności:**
   - Gra oferuje trzy poziomy trudności (_łatwy_, _średni_, _trudny_), które różnią się szybkością przeszkód oraz szerokością przestrzeni pomiędzy rurami.

3. **System monet i skórek:**
   - Monety pojawiają się na trasie, a gracz może je kolekcjonować.
   - Zdobyte monety umożliwiają odblokowanie nowych skórek dla ptaka.

4. **Zapisywanie wyników:**
   - Wyniki graczy są zapisywane w lokalnej bazie danych SQLite.

#### Technologia i Narzędzia
- Kod gry został napisany w **Java 17**.
- **JavaFX**: Framework używany do renderowania grafiki i obsługi interfejsu.
- **SQLite**: Lekka baza danych używana do zapisywania wyników oraz zarządzania zdobytymi monetami i odblokowywaniem skórek.
- **Maven**: Służył jako narzędzie do zarządzania projektem i zależnościami.

### **Architektura**
#### Ogólna Struktura Systemu
Projekt został podzielony na trzy główne warstwy:
1. **Warstwa logiki:**
   - Odpowiada za zasady gry, sterowanie obiektami na ekranie, kolizje między obiektami oraz mechanikę ruchu.
   - Przykładowe klasy:
      - `Bird` – odpowiada za ruch ptaka.
      - `PipeManager` – generuje rury i zarządza ich przesuwaniem oraz kolizjami.
      - `CoinManager` – generuje monety i zarządza ich zbieraniem.

2. **Warstwa graficzna:**
   - Wykorzystuje **JavaFX** do renderowania animacji, tła i elementów interfejsu użytkownika (UI).
   - Przykładowe klasy:
      - `BackgroundRenderer` – renderowanie tła w zależności od pory dnia i poziomu trudności.
      - `GroundRenderer` – animacja przesuwającego się gruntu.
      - `ScoreRenderer` – wizualizacja aktualnego wyniku i liczby zdobytych monet.

3. **Warstwa danych:**
   - Używa **SQLite** do przechowywania trwałych danych.
   - Przykładowe operacje:
      - Zapisywanie wyniku po zakończeniu gry.
      - Zarządzanie odblokowanymi skórkami.
      - Pobieranie wyników gracza na ekranie startowym.

### **Funkcjonalności**
#### Mechanika Gry
1. **Ruch Ptaka:**
   - Klasa `Bird` odpowiada za symulację fizyki: konsekwentny ruch w dół pod wpływem grawitacji oraz możliwość skoku poprzez ustalenie ujemnej prędkości podczas skoku (`jump`).
   - Animacje lotu są obsługiwane przez klasę `BirdAnimator`, która renderuje zmiany pozycji skrzydeł w zależności od prędkości ruchu ptaka.

2. **Przeszkody (Rury):**
   - Generowane dynamicznie przez klasę `PipeManager`. Rury są przesuwane za pomocą stałej prędkości zależnej od poziomu trudności.
   - Po kolizji z rurą gra kończy się.

3. **System Monet:**
   - Klasa `CoinManager` generuje monety na trasie gry w losowych pozycjach. Gracz zdobywa monety poprzez nawigację ptakiem tak, by kolidował z monetą.

#### Poziomy Trudności
- Dostępne poziomy:
   - **Łatwy:** Wolniejsze przesuwanie rur, większe odstępy pomiędzy przeszkodami.
   - **Średni:** Normalne parametry (referujące do klasycznej gry Flappy Bird).
   - **Trudny:** Szybsze przeszkody, mniejsze odstępy.

- Parametry poziomów są ustalane w klasie `PipeManager`.

#### Zapisywanie Wyników
- Gracz wprowadza nazwę w polu tekstowym po zakończeniu gry.
- Wynik, poziom trudności oraz nazwa gracza są zapisywane w bazie danych za pomocą klasy `DatabaseManager`.

#### System Skórek
- Klasa `BirdAnimator` obsługuje dynamiczne ładowanie grafik ptaka na podstawie odblokowanych skórek.
- Skórki są przechowywane jako zasoby w podkatalogach gry.

### **Techniczny opis działania**
#### Przykład Przepływu Danych
1. Gracz naciska klawisz, aby skoczyć ptakiem.
2. Klasa `Bird` aktualizuje pozycję ptaka na osi Y, symulując ruch do góry przeciwko grawitacji.
3. `PipeManager` aktualizuje pozycję rur, przesuwając je w lewo.
4. `CoinManager` sprawdza kolizję ptaka z monetami. Jeśli do niej dochodzi, ilość monet rośnie.
5. Na końcu każdej iteracji gry `GameLoop` renderuje wszystkie obiekty na ekranie i aktualizuje wynik.

### **Przykładowy wzorzec projektowy**
Wzorzec **Game Loop** jest rdzeniem działania gry, odpowiada za cykliczną i synchroniczną aktualizację stanu gry oraz renderowanie elementów na ekranie. W Flappy Bird jest zaimplementowany w klasie `GameLoop`, która dziedziczy po `AnimationTimer` (działa z ok. **60 FPS**).
**Funkcje w pętli:**
1. `update()` – aktualizuje logikę gry (ruch ptaka, rur, monet, generowanie nowych obiektów).
2. `render()` – rysuje wszystkie elementy gry na ekranie.
3. `checkCollisions()` – sprawdza kolizje między ptakiem a rurami, ziemią i monetami.
4. `activateBird()` – rozpoczyna grę na naciśnięcie klawisza spacji.

**Zalety:**
- **Stała prędkość gry:** Pętla działa równomiernie i niezależnie od wydajności urządzenia.
- **Oddzielenie logiki od renderowania:** Ułatwia dodawanie nowej funkcjonalności.
- **Rozszerzalność:** Możliwość łatwego wprowadzania pauzy, efektów i nowych eventów.

### **Wnioski i rozwój**
- Projekt wymagał efektywnego podziału na moduły i współpracy w zespole.
- Połączenie różnych technologii (JavaFX, SQLite) pozwoliło na rozwinięcie funkcjonalności gry.

#### Plany Rozwoju
- Dodanie nowych trybów gry, takich jak endless mode.
- Wprowadzenie power-upów (np. niezniszczalność, podwójne monety).
- Rozbudowa systemu skórek o dodatkowe elementy personalizacji (np. tła).
