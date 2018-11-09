package xnt.com.fun.rom;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.sf.loglib.L;


public class Rom {
    static RomConst currRom;
    static {
        try {
            for(RomConst rom: RomConst.values()){
                if(rom.isThisRomType()){
                    currRom = rom;
                }
            }
        }catch (ActivityNotFoundException exception){
            L.info("Rom","activity not found exception: "+exception);
        }

        if (currRom == null) {
            currRom = RomConst.UNKNOWN;
        }
    }

    public static boolean isRom(RomConst rom) {
        return currRom == rom;
    }

    public static RomAction romAction() {
        return currRom.mRomAction;
    }

    static boolean startSafely(Context context, Intent intent) {
        if (context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return startIntent(context, intent);
        } else {
            return startIntent(context, intent);
        }
    }

    private static boolean startIntent(Context context, Intent intent) {
        try{
            context.startActivity(intent);
            return true;
        } catch (Exception e){
            L.error("EXCEPTION",e.getMessage());
        }
        return false;
    }

    /*****************************
     * 接口定义
     *****************************/
    interface RomType {
        boolean isThisRom();
    }

    public interface RomAction {
        boolean openSetting(Activity activity);
        boolean openFloatWindowSetting(Activity activity);
    }

    public enum RomConst {
        XIAOMI(new NYXiaomiSettingAction()),
        HUAWEI(new NYHuaWeiSettingAction()),
        OPPO(new NYOppoSettingAction()),
        VIVO(new NYVivoSettingAction()),
        FLYME(new NYFlymeSettingAction()),
        MOTO(new NYMotoSettingAction()),
        SMARTTISAN(new NYSmartTisanSettingAction()),
        QIKU(new NYQikuSettingAction()),
        SONY(new NYSonySettingAction()),
        LG(new NYLGSettingAction()),
        LETV(new NYLetvSettingAction()),
        //后续有什么机型就直接在这里加就ok了,务必确保unknown在最后

        UNKNOWN(new BaseSettingAction());

        RomAction mRomAction;
        RomConst(RomAction romAction) {
            this.mRomAction = romAction;
        }

        public boolean isThisRomType() {
            return mRomAction instanceof RomType && ((RomType) mRomAction).isThisRom();
        }
    }
}
