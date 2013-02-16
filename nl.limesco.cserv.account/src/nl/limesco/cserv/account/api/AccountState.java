package nl.limesco.cserv.account.api;

public enum AccountState {
	UNPAID,
	UNCONFIRMED,
	CONFIRMATION_IMPOSSIBLE,
	CONFIRMATION_REQUESTED,
	CONFIRMED,
	DEACTIVATED
}
