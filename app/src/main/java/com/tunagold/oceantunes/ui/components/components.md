# Componenti Material Design

Tutti i componenti MD (Material Design) sono utilizzabili nei fragment.xml dell'applicazione e si trovano in: "com.tunagold.oceantunes.ui,components.your_element". Gli elementi possono seguire due strutture:
1.  Self-closing:
```
 <com.tunagold.oceantunes.ui,components.your_element />
``` 
2.  Non self-closing:
```
<com.tunagold.oceantunes.ui,components.your_element>
	<!-- element body -->
</com.tunagold.oceantunes.ui,components.your_element>
```
Ogni elemento ha degli attributi che permettono di personalizzarli in base alle necessità.

## Attributi Universali

Gli attributi universali sono comuni a tutti gli elementi e applicano loro la medesima trasformazione.

`android:layout_width`: Larghezza dell'elemento misurata in `dp` [standard] o altre unità di misura della lunghezza.

`android:layout_height`: Altezza dell'elemento misurata in `dp` [standard] o altre unità di misura della lunghezza. Per motivi di accessibilità, Material Design suggerisce di assegnare un'altezza minima di `48dp` agli elementi cliccabili (come bottoni, FAB e simili) per motivi di accessibilità.

`android:id`: Identificatore univoco dell'elemento. Segue la struttura: `@+id/id_elemento`.

`android:elevation`: Sposta sull'asse *z* l'elemento in `dp` [standard] o altre unità di misura della lunghezza, permettendo di creare una gerarchia di altezze nel caso in cui più elementi si sovrapponessero. Consente inoltre di proiettare "un'ombra" data dall'elevazione virtuale dell'elemento.

## Button

**Tag name**:  `com.tunagold.oceantunes.ui.components.MaterialButton`.

**Struttura**:  Self-closing - `<com.tunagold.oceantunes.ui.components.MaterialButton />`.

**Stile**: Lo stile di default è filled. Il design di oceantunes non richiede l'applicazione di altri stili.

### Attributi

`app:buttonText: String` - Permette di impostare un testo personalizzato al pulsante. Material Design suggerisce che i testi contenuti nei pulsanti siano in maiuscolo. Il valore di default è: "DEFAULT TEXT BUTTON".

`app:buttonColor: color|reference` - Permette di impostare un colore personalizzato al pulsante. Il valore di default è "@color/purple3".  Il colore del testo è un valore che si adatta al colore dello sfondo. Se lo sfondo è "@color/purple3", il testo diventerà di color "@color/white", altrimenti il testo sarà di colore "@color/dark".

`app:setIcon: Boolean` - Permette di rendere visibile la Trailing Icon nel pulsante. L'icona non può essere modificata ed è "arrow_right".

## Text Field

**Tag name**:  `com.tunagold.oceantunes.ui.components.MaterialTextInput`.

**Struttura**:  Non-self-closing - `<com.tunagold.oceantunes.ui.components.MaterialTextInput></ ...>`.

**Stile**: Lo stile default è filled. Nel design di oceantunes sono previsti text field in stile outilined. Per applicare questo stile è necessario aggiungere l'attributo `style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"` nel tag di apertura.

**Body**:  L'elemento text field richiede un sotto-elemento `TextInputEditText` per  funzionare correttamente:
```
<com.google.android.material.textfield.TextInputEditText  
  android:layout_width="match_parent"  
  android:layout_height="wrap_content"  
  android:inputType="text" />
```
Gli attributi del sotto-elemento sono personalizzabili, ma il design di oceantunes non richiede di modificarli.

### Attributi

`app:boxBackgroundColor: color|reference` - Permette di cambiare il colore dello sfondo del text field. Il valore di default è quello dello stile "outlined", ovvero `#FFFFFF`.

`app:boxStrokeColor: color|reference` - Permette di cambiare il colore del bordo del text field quando è selezionato. Il valore di default è quello dello stile "outlined", ovvero il colore che in `themes.xml` è impostato come "colorPrimary" .

`app:hintText: String` - Permette di modificare il testo di suggerimento del text field. Il valore di default è `Null`, questo permette di utilizzare lo stile outlined senza testo di suggerimento.

`app:hintTextColor: color|reference` - Permette di cambiare il colore del testo di suggerimento del text field. Il valore di default è "@color/dark".

`app:textColor: String` - Permette di cambiare il colore del testo del text field. Il valore di default è "#212121".

`app:endIconMode: String` -Permette di trasformare il text field in un campo di tipo password, assegnando il valore "password_toggle" a questo attributo. Una volta in questa forma, l'elemento viene convertito ed animato come fosse un password field.

## DataBox

**Tag name**:  `com.tunagold.oceantunes.ui.components.DataBox`.

**Struttura**: Self-closing - `<com.tunagold.oceantunes.ui.components.DataBox />`.

**stile**: lo stile di default è filled, è possibile renderlo a 3 segmenti al posto che 2.

### Attributi

`app:is3segment:` Boolean - Permette di decidere se la databox debba contenere o meno 3 campi (Default: False).

`app:title1: String` - Permette di inserire il primo titolo per il primo dato.

`app:title2: String` - Permette di inserire il secondo titolo per il secondo dato.

`app:val1: int` - Permette di inserire il primo valore per il primo dato.

`app:val2: int` - Permette di inserire il secondo valore per il secondo dato.

`app:title3: String` - Nel caso in cui sia un 3 segmenti permette di inserire il terzo titolo per il terzo dato.

`app:val3: int` - Nel caso in cui sia un 3 segmenti permette di inserire il terzo valore per il terzo dato.

## MaterialFAB

***Tag name***: `com.tunagold.oceantunes.ui.components.MaterialFAB`.

***Struttura***: Self-closing - `<com.tunagold.oceantunes.ui.components.MaterialFAB>`.

### Attributi

`android:contentDescription: String` - Permette di inserire una descrizione per il bottone.

`app:icon: Drawable` - Permette di inserire un'immagine all'interno del bottone.

`app:backgroundColor: color` - Permette di cambiare il colore dello sfondo del FAB

`app:icon: reference` - Permette di impostare un icona nel FAB

`app:iconTint: color` - Peremtte di cambiare il colore dell'icona del FAB

## MaterialDivider

***Tag name***: `com.tunagold.oceantunes.ui.components.MaterialDivider`.

***Struttura***: Self-closing - `<com.tunagold.oceantunes.ui.components.MaterialDivider>`.

### Attributi

`app:iconTint: color` - Peremtte di cambiare il colore dell'icona del Divider

## MaterialCarousel

***Tag name*** `com.tunagold.oceantunes.ui.components.carousel.MaterialCarousel`.

***Struttura***: Self-closing - `<com.tunagold.oceantunes.ui.components.carousel.MaterialCarousel>`.

### Attributi

`app:itemSpacing: int` - Permette di scegliere la distanza tra gli elementi del carosello.

## SongSummmary

***Tag name***: `com.tunagold.oceantunes.ui.components.SongSummary`.

***Struttura***: Self-closing - `<com.tunagold.oceantunes.ui.components.SongSummary>`.

### Attributi

`app:isLarge: Boolean` - Permette di scegliere la dimensione del riassunto canzone per adattarlo al carosello a una scheda dei dettagli canzone.

`app:artist: String` - Permette di inserire il nome dell'artista della canzone.

`app:image: Drawable` - Permette di inserire l'immagine della copertina della canzone.

`app:title: String` - Permette di inserire il titolo della canzone.

## TopChartsSong

***Tag name***: `com.tunagold.oceantunes.ui.components.TopChartsSong`.

***Struttura***: Self-Closing `<com.tunagold.oceantunes.ui.components.TopChartsSong>`.

### Attributi

`app:artistchartssong: String` - Permette di inserire il nome dell'artista.

`app:titlechartssong: String` - Permette di inserire il titolo della canzone.

`app:imgchartssong: Drawable` - Permette di inserire l'immagine della canzone.

## Slider

***Struttura***: Self-closing `<com.google.android.material.slider.Slider>`.

***stile***: slider discreto

### Attributi

`android:stepSize: float` - Permette di decidere di quanto ci si sposta tra un valore e il successivo nello slider.

`android:valueFrom: float` - Permette di decidere il valore minimo dello slider.

`android:valueTo: float` - Permette di decidere il valore massimo dello slider.

## MaterialRating

**Tag name**: `com.tunagold.oceantunes.ui.components.MaterialRating`.

**Struttura**: Self-closing - `<com.tunagold.oceantunes.ui.components.MaterialRating />`.

**Descrizione**: Un componente RatingBar personalizzato che permette agli utenti di selezionare una valutazione con un numero di stelle predefinito. Mostra un Toast con la valutazione selezionata quando l'utente modifica il valore.

### Attributi

`android:numStars: int` - Permette di impostare il numero di stelle visibili (default: 5).

`android:stepSize: float` - Permette di impostare la granularità della valutazione (default: 0.5).

`android:isIndicator: Boolean` - Permette di impostare se il RatingBar è solo in modalità visualizzazione (default: false).

### Metodi

`getRatingValue(): Float` - Restituisce il valore corrente della valutazione.

`setRatingValue(rating: Float)` - Imposta il valore della valutazione.

## Elipses

**Tag name**: `com.tunagold.oceantunes.ui.components.Elipses`.

**Struttura**: Self-closing - `<com.tunagold.oceantunes.ui.components.Elipses />`.

**Descrizione**: Un componente personalizzato che rappresenta un pulsante con un'icona di ellissi (tre puntini verticali). Quando viene cliccato, permette di mostrare o nascondere una vista associata (ad esempio, una card di impostazioni).

### Attributi

`app:elipsesIcon: reference` - Permette di impostare un'icona personalizzata per il pulsante. Il valore di default è `R.drawable.ic_vertdots_fillblack_24dp`.

### Comportamento

- Al click, il componente alterna la visibilità di una vista associata (ad esempio, una card di impostazioni) tra `VISIBLE` e `GONE`.
- La vista associata viene cercata tramite l'ID `R.id.settingsCard` nel layout genitore.

## SettingsDrawer

**Tag name**: `com.tunagold.oceantunes.ui.components.Settings`.

**Struttura**: Non self-closing - `<com.tunagold.oceantunes.ui.components.Settings></ ...>`.

**Descrizione**: Un componente personalizzato che rappresenta un drawer di impostazioni con sette campi di testo configurabili. Ogni campo di testo può essere personalizzato tramite attributi specifici.

### Attributi

`app:setting1: String` - Permette di impostare il testo per il primo campo di impostazioni.

`app:setting2: String` - Permette di impostare il testo per il secondo campo di impostazioni.

`app:setting3: String` - Permette di impostare il testo per il terzo campo di impostazioni.

`app:setting4: String` - Permette di impostare il testo per il quarto campo di impostazioni.

`app:setting5: String` - Permette di impostare il testo per il quinto campo di impostazioni.

`app:setting6: String` - Permette di impostare il testo per il sesto campo di impostazioni.

`app:setting7: String` - Permette di impostare il testo per il settimo campo di impostazioni.

### Comportamento

- Il componente infla il layout `R.layout.item_layout_settings` e popola i campi di testo con i valori forniti tramite gli attributi.

## MaterialSearchBar

**Tag name**: `com.tunagold.oceantunes.ui.components.MaterialSearchBar`.

**Struttura**: Non self-closing - `<com.tunagold.oceantunes.ui.components.MaterialSearchBar></ ...>`.

**Descrizione**: Un componente personalizzato che rappresenta una barra di ricerca con un campo di input e funzionalità integrate per la gestione delle azioni di ricerca e cancellazione.

### Attributi

- **Nessun attributo specifico**: Il componente utilizza un layout predefinito (`R.layout.searchbar_layout`) e non richiede attributi personalizzati.

### Metodi

`getQuery(): String` - Restituisce il testo attualmente inserito nella barra di ricerca.

`clear()` - Cancella il testo nella barra di ricerca e invoca l'azione di cancellazione, se definita.

### Comportamento

- **Ricerca**: Quando l'utente preme l'azione di ricerca sulla tastiera (IME_ACTION_SEARCH), il componente invoca la lambda `onSearchAction` con il testo inserito.
- **Cancellazione**: Il metodo `clear()` cancella il testo e invoca la lambda `onClearAction`, se definita.

### Callback

`onSearchAction: ((String) -> Unit)?` - Callback invocato quando l'utente esegue una ricerca, passando il testo inserito.

`onClearAction: (() -> Unit)?` - Callback invocato quando il testo viene cancellato tramite il metodo `clear()`.