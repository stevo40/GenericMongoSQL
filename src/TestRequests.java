import java.util.ArrayList;

public class TestRequests {


	public static ArrayList<String> testPathConfigs() throws Exception {
		
		ArrayList<String> pathConfigs = new ArrayList<String>();

		
		
//		pathConfigs.add("gscrm");
//		pathConfigs.add("!collection:animal_count:_id:519be5ed3eb9a874bb4c0341:true");
//		pathConfigs.add("!collection:animal_count:*:*:false");
//		pathConfigs.add("animal_count.*");
		
//		pathConfigs.add("!collection:genotype_counts:*:*:false");
//		pathConfigs.add("genotype_counts.name");
		
//		pathConfigs.add("!collection:genotype_counts:name:Collie:false");
//		pathConfigs.add("genotype_counts.loci.025_MDR1.*");
		
//		pathConfigs.add("!collection:genotype_counts:name:Collie:false");
//		pathConfigs.add("genotype_counts.*");
		
//		pathConfigs.add("wp");
//		pathConfigs.add("!collection:sample:*:*:false");
//		pathConfigs.add("sample.resultString.asjson");
//		pathConfigs.add("sample.aspretty");
		
		
		
		// script 1:
//		pathConfigs.add("gscrm");
//		pathConfigs.add("#mycatdna");
//		pathConfigs.add("!collection:user:email:cmanctil@gmail.com:false");
//		pathConfigs.add("user._id");
//		pathConfigs.add("user.firstName");
//		pathConfigs.add("user.lastName");
//		pathConfigs.add("!collection:animal:owner:$user/_id:true");
//		pathConfigs.add("animal.passNumber");
//		pathConfigs.add("animal.registeredName");
//		pathConfigs.add("animal.name");
//		pathConfigs.add("animal.product");
//		pathConfigs.add("animal.analysisCountry");
//		pathConfigs.add("animal.breed.name:lang");
		
		pathConfigs.add("gscrm");
//		pathConfigs.add("mycatdna");
//		pathConfigs.add("!collection:animal:breed.name.os:Cornish Rex:false");
//		pathConfigs.add("!collection:animal:breed.name.os:Cornish Rex:false");
//		pathConfigs.add("!collection:animal:breed.name.en:Bergamasco:false");
		pathConfigs.add("!collection:animal:passNumber:870027408083:false");
//		pathConfigs.add("animal.owner");
//		pathConfigs.add("animal.asobject");
//		pathConfigs.add("animal.aspretty");
		
//		pathConfigs.add("!collection:user:_id:$animal.owner:true");
//		pathConfigs.add("user.asjson");
		
		
		
		pathConfigs.add("animal.passNumber");
		pathConfigs.add("animal.analyses.genotypeSet");
		
		pathConfigs.add("!collection:genotypeResultSet:_id:$animal.analyses/genotypeSet:true");
		pathConfigs.add("genotypeResultSet._id");
		pathConfigs.add("genotypeResultSet.passNumber");
		pathConfigs.add("genotypeResultSet.genotypes.147_SRMA_chr8_risklocus_SNP4.0");
		pathConfigs.add("genotypeResultSet.genotypes.147_SRMA_chr8_risklocus_SNP4.1");
		
//		pathConfigs.add("animal.passNumber");
		
		
//		pathConfigs.add("!collection:breed:*:519248a83cd390a0520000ac:false");
//		pathConfigs.add("breed._id");
//		pathConfigs.add("!collection:animal:breed._id:$breed/_id:true");
//		pathConfigs.add("animal.passNumber");
//		pathConfigs.add("animal.analyses.sample_call_rate");
//		pathConfigs.add("animal.analyses.sample_qc_status");
//		pathConfigs.add("animal.report.published");
		
		
//		pathConfigs.add("!collection:animal:breed.name.en:Abyssinian:false");
//		pathConfigs.add("animal.analyses.asobject");
//		pathConfigs.add("animal.passNumber");
//		pathConfigs.add("animal.breed.name.en");
		
//		pathConfigs.add("!collection:breed:breed.name.en:Komondor:false");
//		pathConfigs.add("!collection:breed:breed.name.en:White Swiss Shepherd Dog:false");
//		pathConfigs.add("breed.name.en");
//		pathConfigs.add("breed._id");
		
		
//		pathConfigs.add("animal.dnaIdData.data.*");
		
//		pathConfigs.add("animal.dnaIdData.data.AHT121");
//		pathConfigs.add("animal.dnaIdData.data.AHT137");
//		pathConfigs.add("animal.dnaIdData.data.AHTK211");
//		pathConfigs.add("animal.dnaIdData.data.AHTh130");
//		pathConfigs.add("animal.dnaIdData.data.AHTh171");
//		pathConfigs.add("animal.dnaIdData.data.AHTh260");
//		pathConfigs.add("animal.dnaIdData.data.AHTk253");
//		pathConfigs.add("animal.dnaIdData.data.CXX279");
//		pathConfigs.add("animal.dnaIdData.data.FH2054");
//		pathConfigs.add("animal.dnaIdData.data.FH2848");
//		pathConfigs.add("animal.dnaIdData.data.INRA21");
//		pathConfigs.add("animal.dnaIdData.data.INU005");
//		pathConfigs.add("animal.dnaIdData.data.INU030");
//		pathConfigs.add("animal.dnaIdData.data.INU055");
//		pathConfigs.add("animal.dnaIdData.data.REN105LO3");
//		pathConfigs.add("animal.dnaIdData.data.REN162C04");
//		pathConfigs.add("animal.dnaIdData.data.REN169D01");
//		pathConfigs.add("animal.dnaIdData.data.REN169O18");
//		pathConfigs.add("animal.dnaIdData.data.REN247M23");
//		pathConfigs.add("animal.dnaIdData.data.REN54P11");
//		pathConfigs.add("animal.dnaIdData.data.REN64E19");
//		pathConfigs.add("animal.dnaIdData.data.Amelogenin");


		
//		pathConfigs.add("!collection:animal:*:*:false");
//		pathConfigs.add("animal.passNumber");
//		pathConfigs.add("animal.photos.0.large");
//		pathConfigs.add("animal.photos.1.large");
//		pathConfigs.add("animal.photos.2.large");
//		pathConfigs.add("animal.photos.3.large");
//		pathConfigs.add("animal.photos.4.large");
//		pathConfigs.add("animal.photos.5.large");
//		pathConfigs.add("animal.photos.6.large");
//		pathConfigs.add("animal.photos.7.large");
//		pathConfigs.add("animal.photos.8.large");
//		pathConfigs.add("animal.photos.9.large");
		
//		!collection:animal:passNumber:870045415359:false
//		animal.passNumber
//		animal.analyses.genotypeSet
//		!collection:genotypeResultSet:_id:$animal/analyses/genotypeSet:true
//		genotypeResultSet._id
		
		
//		Cornish Rex
		
		return pathConfigs;
	}

}
