
# ejecucion

el programa para ejecutar necesita dos parametos

- `-DrutaFuente` : ruta de los directorios donde se guardan los ats subidos pro el protal
- `-DrutaDestino` : ruta de de la carpeta compartida donde se replica al servidor 10.185.2.142

# archivo ".bat" de ejecucion

```bash
set PATH=C:\Program Files\Java\jdk1.8.0_161\bin

C:

cd C:\opt\mover-ats-to-directorio-compratido

java -jar -DrutaFuente="C:\\ATS" -DrutaDestino="\\10.185.2.142\datafs"  mover-ats-to-directorio-compratido-1.0-SNAPSHOT.jar >> logl.log
```