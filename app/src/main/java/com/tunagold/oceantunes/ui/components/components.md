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