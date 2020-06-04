package com.example.batchprocessing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import it.ubiquity.gestorebatch.dataobjects.icbpi.*;
import it.ubiquity.gestoreprofilazione.dataobjects.ContrattoInformativo;
import it.ubiquity.gestoreprofilazione.dataobjects.RichiestaProfiloIscrizione;
import org.apache.log4j.Logger;

import it.ubiquity.gestorealert.dataobjects.Azienda;
import it.ubiquity.gestoredb.client.RMICaller;
import it.ubiquity.gestoredb.rmi.common.InputObjectMultiDB;
import it.ubiquity.gestoredb.rmi.common.OutputObject;
import it.ubiquity.gestoredb.rmi.common.ResponseObject;
import it.ubiquity.gestoreprofilazione.dataobjects.Esito;

@SuppressWarnings("unused")
public class GestoreDbWrapper {
	private Logger logger = Logger.getLogger(GestoreDbWrapper.class);

	private final static String MEMORIZZA_ELABORAZIONE_BATCH = "memorizzaElaborazioneBatch";
	private final static String AGGIORNA_ELABORAZIONE_BATCH = "aggiornaElaborazioneBatch";

	private final static String RECUPERA_DATA_ULTIMA_ELABORAZIONE = "recuperaDataUltimaElaborazione";
	private final static String CARICA_ANAGRAFICHE_ODS = "caricaAnagraficheODS";
	private final static String VERIFICA_PREFISSO = "verificaPrefisso";
	private final static String MEMORIZZA_PERIODO_ELABORAZIONE = "memorizzaUltimaElaborazione";
	private final static String CARICA_PRODOTTI = "caricaProdotti";	
	
	private final static String MEMORIZZA_DETTAGLIO_ODS = "memorizzaDettaglioODS";
	private final static String CARICA_ANAGRAFICHE_SION = "caricaAnagraficheSION";
	private final static String AGGIORNA_ANAGRAFICA_SION = "aggiornaAnagraficaSION";
	private final static String MEMORIZZA_DETTAGLIO_SION = "memorizzaDettaglioSION";
	private final static String MEMORIZZA_DETTAGLIO_AGGIORNA_PROFILO_CARTE = "memorizzaDettaglioAggiornaProfiloCarte";
	private final static String MEMORIZZA_DETTAGLIO_DODICI_MESI = "memorizzaDettaglioDodiciMesi";
//	private final static String MEMORIZZA_DETTAGLIO_REMIND = "memorizzaDettaglioRemind";

	private final static String CARICA_ANAGRAFICHE_MIGRAZIONE_CARTE = "caricaAnagraficheMigrazioneCarte";
	private final static String ANAGRAFICHE_ALLINEAMENTO_FEE_DODICI_MESI = "caricaAnagraficheAllineamentoFeeDodiciMesi";
//	private final static String ANAGRAFICHE_REMIND = "caricaAnagraficheRemind";
	private final static String MEMORIZZA_DETTAGLIO_MIGRAZIONE_CARTE = "memorizzaDettaglioMigrazioneCarte";
	private final static String AGGIORNA_ANAGRAFICA_MIGRAZIONE_CARTE = "aggiornaAnagraficaMigrazioneCarte";
	private final static String LOAD_MOST_RECENT_ACTIVE_CUSTOMER = "loadMostRecentActiveCustomerKCByAccountAndPan";

	private final static String CARICA_ANAGRAFICHE_AGGIORNA_PROFILO_CARTE = "caricaAnagraficheAggiornaProfiloCarte";
	private final static String CARICA_CONTRATTO_INFORMATIVO_AGGIORNA_PROFILO_CARTE = "caricaContrattoInformativoAggiornaProfiloCarte";
	private final static String CARICA_BILLING_FEE = "caricaBilling";
	private final static String UPDATE_ANAGRAFICHE_AGGIORNA_PROFILO_CARTE = "updateAnagraficheAggiornaProfiloCarte";

	private final static String NO_RESPONSE_ERROR = "Nessuna risposta ricevuta.";
	private final static String NO_DATA_ERROR = "Nessun dato recuperato.";
	
	private final static String AZZERA_SPESA_GIORNALIERA = "azzeraSpesaGiornaliera";
	private final static String AZZERA_SPESA_MENSILE = "azzeraSpesaMensile";

	private final static String ELIMINA_SERVIZI_PAYSI_PEND = "eliminaServiziPaySIPend";
	private final static String RESETTA_INVIO_MENSILE_DISPONIBILITA = "resettaInvioMensileDisponibilita";
	
	private final static String CARICA_CONTRATTI_INFORMATIVI_ATTIVI_SYNC_ODS = "caricaContrattiInformativiAttiviSyncODS";
	private final static String CARICA_PAN_CONTRATTI_INFORMATIVI_ATTIVI_TRIMESTRE_SYNC_ODS = "caricaPanContrattiInformativiAttiviTrimestreSyncODS";
	private final static String CARICA_ANAGRAFICA_PER_PAN_SYNC_ODS = "caricaAnagraficaPerPanSyncODS";
	private final static String AGGIORNA_CARTA_SYNC_ODS = "aggiornaCartaSyncODS";
	
	private final static String CARICA_ANAGRAFICHE_PER_PRODOTTO_ODS = "caricaAnagrafichePerProdottoODS";
	private final static String RECUPERA_DATA_ULTIMA_ELABORAZIONE_BATCH = "recuperaDataUltimaElaborazioneBatch";
	private final static String CARICA_SOGLIE_ISCRIZIONE_PER_STATO = "caricaLeSoglieIscrizionePerStato";
	private final static String RECUPERA_BIN_HCE = "recuperaBinHCE";

	private final static String GET_PROFILO_DINAMICO = "getProfiloDinamico";

	public final static int NO_DATA = 100;
	public final static int GENERIC = 101;
	public final static int NO_RESPONSE = 102;
	
	private Properties rmiConfig;

	public GestoreDbWrapper() {

	}

	/**
	 * Operazioni comuni ai metodi che devono recuperare un solo valore dal db.
	 * @param operation String
	 * @param input InputObjectMultiDB
	 * @return esito
	 */
	private Esito caricaDatoSingolo(String operation, InputObjectMultiDB input) {
		RMICaller dbCaller = RMICaller.getInstance(rmiConfig);
		if (dbCaller == null)
			return Esito.getEsitoNegativo(1, "Il client RMI non è disponibile.");

		ResponseObject response = dbCaller.callGestoreDBOperationMultiDB(operation, input);
		if (response != null) {
		    	System.out.println("caricaDatoSingolo - response != null");
			if (response.getObjectOut() != null && response.getResponseMessage().equals(ResponseObject.RESPONSE_OK)) {
				OutputObject output = response.getObjectOut();
				List<Object> results = output.getParams();
				if (results.size() == 1) {
					return Esito.getEsitoPositivo(0, results.get(0));
				} else {
					logger.info(NO_DATA_ERROR);
					return Esito.getEsitoNegativo(NO_DATA, operation + " " + NO_DATA_ERROR);
				}
			} else {
				String error = "Errore: " + response.getErrorMessage();
				logger.error(error);
				return Esito.getEsitoNegativo(GENERIC, error);
			}
		} else {
			logger.error(NO_RESPONSE_ERROR);
			return Esito.getEsitoNegativo(NO_RESPONSE, NO_RESPONSE_ERROR);
		}
	}

	/**
	 * Le operazioni comuni ai metodi che effettuano un'operazione su DB senza restituire un risultato, che non sia
	 * l'esito dell'operazione stessa.
	 * @param operation String
	 * @param input InputObjectMultiDB
	 * @return esito
	 */
	private Esito eseguiOperazione(String operation, InputObjectMultiDB input) {
		RMICaller dbCaller = RMICaller.getInstance(rmiConfig);
		if (dbCaller == null)
			return Esito.getEsitoNegativo(1, "Il client RMI non è disponibile.");

		ResponseObject response = dbCaller.callGestoreDBOperationMultiDB(operation, input);
		if (response != null) {
			if (ResponseObject.RESPONSE_OK.equals(response.getResponseMessage())) {
				return Esito.getEsitoPositivo(0, null);
			} else {
				String error = "Errore: " + response.getErrorMessage();
				logger.error(error);
				return Esito.getEsitoNegativo(NO_DATA, error);
			}
		} else {
			logger.error(NO_RESPONSE_ERROR);
			return Esito.getEsitoNegativo(NO_RESPONSE, NO_RESPONSE_ERROR);
		}
	}
	
	public Esito memorizzaElaborazioneBatch(String tipologiaBatch,
			StatoBatch stato, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(tipologiaBatch);
		parameters.add(stato);
		parameters.add(azienda);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(MEMORIZZA_ELABORAZIONE_BATCH, input);
	}
	
	public Esito aggiornaElaborazioneBatch(String codiceBatch, StatoBatch stato, Long numeroEstratti, String descrizioneErrore, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(codiceBatch);
		parameters.add(stato);
		parameters.add(numeroEstratti);
		parameters.add(descrizioneErrore);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(AGGIORNA_ELABORAZIONE_BATCH, input);
	}
	
	public Esito memorizzaDettaglioODS(AnagraficaOds anagraficaOds,	StatoDettaglio stato, String codiceBatch, String descrizioneErrore, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(anagraficaOds);
		parameters.add(stato);
		parameters.add(codiceBatch);
		parameters.add(descrizioneErrore);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(MEMORIZZA_DETTAGLIO_ODS, input);
	}


	public Esito caricaAnagraficheOds(long numeroGiorniDisallineamento, int intervalloInserimentoODS, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(numeroGiorniDisallineamento);
		parameters.add(intervalloInserimentoODS);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(CARICA_ANAGRAFICHE_ODS, input);
	}

	/**
	 * carica i bin HCE da DB CRM TB_BLOCCO_SMS_PER_BANCA
	 * @param azienda Azienda
	 * @return it.ubiquity.gestoreprofilazione.dataobjects.Esito
	 */
	public Esito caricaBinHCE(Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(RECUPERA_BIN_HCE, input);
	}
	
	
	public Esito caricaAnagrafichePerProdottoOds(String abi, String affinity, String codicePlastica, long numeroGiorniDisallineamento, int intervalloInserimentoODS, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(numeroGiorniDisallineamento);
		parameters.add(intervalloInserimentoODS);
		parameters.add(abi);
		parameters.add(affinity);
		parameters.add(codicePlastica);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(CARICA_ANAGRAFICHE_PER_PRODOTTO_ODS, input);
	}	
	
	public Esito recuperaDataUltimaElaborazione(Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(RECUPERA_DATA_ULTIMA_ELABORAZIONE, input);
	}
	
	public Esito verificaPrefisso(int prefisso, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(prefisso);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(VERIFICA_PREFISSO, input);
	}
	
	
	public Esito memorizzaPeriodoElaborazione(long numeroTotaleInseriti, long numeroTotaleNonInseriti, long numeroAnagrafiche, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(numeroTotaleInseriti);
		parameters.add(numeroTotaleNonInseriti);
		parameters.add(numeroAnagrafiche);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(MEMORIZZA_PERIODO_ELABORAZIONE, input);
	}
	
	
	public Esito caricaProdotti(String tipologia, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(tipologia);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(CARICA_PRODOTTI, input);
	}
	
	public Esito caricaAnagraficaSion(String azione, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(azione);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_ANAGRAFICHE_SION, input);
	}

	public Esito aggiornaAnagraficaSion(String pan, String stato, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pan);
		parameters.add(stato);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return eseguiOperazione(AGGIORNA_ANAGRAFICA_SION, input);
	}


	public Esito memorizzaDettaglioSion(AnagraficaSion anagraficaSion,
			StatoDettaglio stato, String codiceBatch, String descrizioneErrore, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(anagraficaSion);
		parameters.add(stato);
		parameters.add(codiceBatch);
		parameters.add(descrizioneErrore);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(MEMORIZZA_DETTAGLIO_SION, input);
	}


	public Esito caricaAnagraficheMigrazioneCarte(String azione, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(azione);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_ANAGRAFICHE_MIGRAZIONE_CARTE, input);
	}

	public Esito memorizzaDettaglioMigrazioneCarte(AnagraficaMigrazioneCarte anagraficaSion, StatoDettaglio stato, String codiceBatch, String descrizioneErrore, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(anagraficaSion);
		parameters.add(stato);
		parameters.add(codiceBatch);
		parameters.add(descrizioneErrore);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(MEMORIZZA_DETTAGLIO_MIGRAZIONE_CARTE, input);
	}


	public Esito aggiornaAnagraficaMigrazioneCarte(String pan, String stato, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pan);
		parameters.add(stato);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return eseguiOperazione(AGGIORNA_ANAGRAFICA_MIGRAZIONE_CARTE, input);
	}


	public Esito loadMostRecentActiveCustomerKCByAccountAndPan(String account, String pan, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(account);
		parameters.add(pan);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(LOAD_MOST_RECENT_ACTIVE_CUSTOMER, input);
	}

	public Esito caricaAnagraficheAllineamentoFeeDodiciMesi(Integer intervalloGiorni, Integer partitionNumber, Integer maxNumberOfPartitions, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(partitionNumber);
		parameters.add(intervalloGiorni);
		parameters.add(maxNumberOfPartitions);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(ANAGRAFICHE_ALLINEAMENTO_FEE_DODICI_MESI, input);
	}

//	public Esito caricaAnagraficheRemind(Azienda azienda, Integer days) {
//		InputObjectMultiDB input = new InputObjectMultiDB();
//		List<Object> parameters = new ArrayList<Object>();
//		parameters.add(days);
//		input.setParams(parameters);
//		input.setGroupCompany(azienda.getGroupCompany());
//		return caricaDatoSingolo(ANAGRAFICHE_REMIND, input);
//	}

	public Esito azzeraSpesaGio(Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return eseguiOperazione(AZZERA_SPESA_GIORNALIERA, input);
	}
	
	public Esito azzeraSpesaMens(Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return eseguiOperazione(AZZERA_SPESA_MENSILE, input);
	}

	public Esito eliminaServiziPaySiP(Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return eseguiOperazione(ELIMINA_SERVIZI_PAYSI_PEND, input);
	}

	public Esito resettaInvioMensileDispo(Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return eseguiOperazione(RESETTA_INVIO_MENSILE_DISPONIBILITA, input);
	}	
    
	public Esito caricaPanContrattiInformativiAttODS(Configurazione configurazione, Azienda azienda){
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(configurazione);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_CONTRATTI_INFORMATIVI_ATTIVI_SYNC_ODS, input);
	}

	public Esito caricaAnaPerPanSyncODS(String pan, Azienda azienda){
		
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pan);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_ANAGRAFICA_PER_PAN_SYNC_ODS , input);
	}
	
	public Esito aggiornaCartaSyncODS(String pan, AnagraficaOds anagraficaOds, Azienda azienda){
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pan);
		parameters.add(anagraficaOds);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return eseguiOperazione(AGGIORNA_CARTA_SYNC_ODS, input);
	}

	public Esito caricaPanContrattiTriInformativiAttODS(Configurazione configurazione, Azienda azienda){
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(configurazione);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_PAN_CONTRATTI_INFORMATIVI_ATTIVI_TRIMESTRE_SYNC_ODS, input);
	}

	public Esito caricaAnagraficheAggiornaProfiloCarte(Azienda azienda, int limit) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(limit);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_ANAGRAFICHE_AGGIORNA_PROFILO_CARTE, input);
	}

	public Esito recuperaContrattoInformativo(Azienda azienda, String pan, String coTel) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pan);
		parameters.add(coTel);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_CONTRATTO_INFORMATIVO_AGGIORNA_PROFILO_CARTE, input);
	}

	public Esito getBillingFee(Azienda azienda, String pan) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pan);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(CARICA_BILLING_FEE, input);
	}

	public Esito updateAnagraficheAggiornaProfiloCarte(Azienda azienda, String pan, String update) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pan);
		parameters.add(update);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());
		return caricaDatoSingolo(UPDATE_ANAGRAFICHE_AGGIORNA_PROFILO_CARTE, input);
	}

	public Esito memorizzaDettaglioAggiornaProfiloCarte(ContrattoInformativo contrattoInformativo, StatoDettaglio stato, String codiceBatch, String descrizioneErrore, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(contrattoInformativo);
		parameters.add(stato);
		parameters.add(codiceBatch);
		parameters.add(descrizioneErrore);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return eseguiOperazione(MEMORIZZA_DETTAGLIO_AGGIORNA_PROFILO_CARTE, input);
	}

	public Esito memorizzaDettaglioDodiciMesi(ContrattoInformativo contrattoInformativo, StatoDettaglio stato, String codiceBatch, String descrizioneErrore, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(contrattoInformativo);
		parameters.add(stato);
		parameters.add(codiceBatch);
		parameters.add(descrizioneErrore);

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return eseguiOperazione(MEMORIZZA_DETTAGLIO_DODICI_MESI, input);
	}

//	public Esito memorizzaDettaglioRemind(ContrattoInformativo contrattoInformativo, StatoDettaglio stato, String codiceBatch, String descrizioneErrore, Azienda azienda) {
//		InputObjectMultiDB input = new InputObjectMultiDB();
//		List<Object> parameters = new ArrayList<Object>();
//		parameters.add(contrattoInformativo);
//		parameters.add(stato);
//		parameters.add(codiceBatch);
//		parameters.add(descrizioneErrore);
//		input.setParams(parameters);
//		input.setGroupCompany(azienda.getGroupCompany());
//		return eseguiOperazione(MEMORIZZA_DETTAGLIO_REMIND, input);
//	}
	
	public Esito recuperaDataUltimaElaborazioneBatch(String identificativoBatchParteIniziale, String identificativoBatchParteFinale, String stato, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(identificativoBatchParteIniziale);
		parameters.add(identificativoBatchParteFinale);
		parameters.add(stato);
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(RECUPERA_DATA_ULTIMA_ELABORAZIONE_BATCH, input);
	}	
	
	public Esito caricaSoglieIscrizioneProdotti(int statoSoglie, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();
		
		parameters.add(statoSoglie);
		
		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(CARICA_SOGLIE_ISCRIZIONE_PER_STATO, input);
	}

	/**
	 * Recovers tipoProfilo from TB_PROFILI_DINAMICI
	 * @param richiestaProfiloIscrizione RichiestaProfiloIscrizione
	 * @param azienda Azienda
	 * @return caricaDatoSingolo(GET_PROFILO_DINAMICO, input);
	 */
	public Esito getProfiloDinamico(RichiestaProfiloIscrizione richiestaProfiloIscrizione, Azienda azienda) {
		InputObjectMultiDB input = new InputObjectMultiDB();
		List<Object> parameters = new ArrayList<Object>();

		parameters.add(richiestaProfiloIscrizione.getAbi());
		parameters.add(richiestaProfiloIscrizione.getCodiceAffinity());
		parameters.add(richiestaProfiloIscrizione.getCodicePlastica());
		parameters.add(richiestaProfiloIscrizione.getBin());
		parameters.add(richiestaProfiloIscrizione.getPrefisso());
		parameters.add(richiestaProfiloIscrizione.getTipoPosizione());
		parameters.add(richiestaProfiloIscrizione.getTipoCondizione());

		input.setParams(parameters);
		input.setGroupCompany(azienda.getGroupCompany());

		return caricaDatoSingolo(GET_PROFILO_DINAMICO, input);
	}
}