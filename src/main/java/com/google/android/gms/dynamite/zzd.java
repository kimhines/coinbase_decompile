package com.google.android.gms.dynamite;

import android.content.Context;
import com.google.android.gms.dynamite.DynamiteModule.zzc;

final class zzd implements com.google.android.gms.dynamite.DynamiteModule.zzd {
    zzd() {
    }

    public final zzj zza(Context context, String str, zzi com_google_android_gms_dynamite_zzi) throws zzc {
        zzj com_google_android_gms_dynamite_zzj = new zzj();
        com_google_android_gms_dynamite_zzj.zzgpy = com_google_android_gms_dynamite_zzi.zzad(context, str);
        com_google_android_gms_dynamite_zzj.zzgpz = com_google_android_gms_dynamite_zzi.zzb(context, str, true);
        if (com_google_android_gms_dynamite_zzj.zzgpy == 0 && com_google_android_gms_dynamite_zzj.zzgpz == 0) {
            com_google_android_gms_dynamite_zzj.zzgqa = 0;
        } else if (com_google_android_gms_dynamite_zzj.zzgpy >= com_google_android_gms_dynamite_zzj.zzgpz) {
            com_google_android_gms_dynamite_zzj.zzgqa = -1;
        } else {
            com_google_android_gms_dynamite_zzj.zzgqa = 1;
        }
        return com_google_android_gms_dynamite_zzj;
    }
}
