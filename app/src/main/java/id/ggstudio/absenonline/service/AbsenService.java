package id.ggstudio.absenonline.service;

import id.ggstudio.absenonline.model.Absen;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AbsenService {

    @POST("api/absen/create")
    Call<Absen> postAbsen(@Body Absen absen);

}
