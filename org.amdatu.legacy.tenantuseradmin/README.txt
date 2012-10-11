ORG.AMDATU.LEGACY.TENANTUSERADMIN
=================================

This bundle is LEGACY and needed to keep the deprecated tenant-mechanism of Amdatu
working. Without this bundle, the OpenSocial container won't start. 

What this bundle does is simply publish an aspect on UserAdmin to add the default
tenant identifier ("tenant_id", should be set to "Default"), needed for the other 
tenant-aware services.

