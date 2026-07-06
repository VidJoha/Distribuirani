#!/usr/bin/env bash
# Demo detektora grešaka ♦P (Algoritam 15.6): otvara 4 terminala.

cd "$(dirname "$0")" || exit 1

run() {   # run "naslov" komanda...
  local title="$1"; shift
  alacritty -T "$title" -e bash -c "cd '$PWD'; $*; echo; echo '[gotovo — Enter za zatvaranje]'; read" &
}

run "NameServer" "java -cp . com.mycompany.projektdist.NameServer"
sleep 2
run "p0" "java -cp . com.mycompany.projektdist.FailureDetectorTester demo 0 3 normal"
run "p1" "java -cp . com.mycompany.projektdist.FailureDetectorTester demo 1 3 normal"
run "p2" "java -cp . com.mycompany.projektdist.FailureDetectorTester demo 2 3 slow:8"
