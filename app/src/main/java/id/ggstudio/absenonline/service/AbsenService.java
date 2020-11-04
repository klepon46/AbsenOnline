package id.ggstudio.absenonline.service;

import java.util.List;

import id.ggstudio.absenonline.model.Absen;
import id.ggstudio.absenonline.model.Company;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AbsenService {

    @POST("api/absen/create")
    Call<Absen> postAbsen(@Body Absen absen);

    @GET("api/absen/company")
    Call<List<Company>> getCompanies();

}
