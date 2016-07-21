package com.clemSP.iteration1.model;

import com.clemSP.iteration1.R;

import java.util.ArrayList;
import java.util.List;


public class ImageFeatureManager
{
    public static List<ImageFeature> getRelatedImages(AppAttribute attribute)
    {
        switch (attribute)
        {
            case Detective: return getDetectiveImages();
            case Weapon: return getWeaponImages();
            case Location: return getSettingImages();
            case Murderer: return getMurdererImages();
            case Victim: return getVictimImages();
        }
        return null;
    }


    private static List<ImageFeature> getDetectiveImages()
    {
        List<ImageFeature> detectiveImages = new ArrayList<>(7);

        detectiveImages.add(new ImageFeature(R.drawable.unknown, R.string.colonel_race));
        detectiveImages.add(new ImageFeature(R.drawable.detective_hercule_poirot, R.string.hercule_poirot));
        detectiveImages.add(new ImageFeature(R.drawable.detective_miss_marple, R.string.miss_marple));
        detectiveImages.add(new ImageFeature(R.drawable.detective_mystery_novel, R.string.mystery_novel));
        detectiveImages.add(new ImageFeature(R.drawable.detective_superintendent_battle, R.string.superintendent_battle));
        detectiveImages.add(new ImageFeature(R.drawable.detective_tommy_tuppence, R.string.tommy_tuppence));
        detectiveImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown));

        return detectiveImages;
    }


    private static List<ImageFeature> getWeaponImages()
    {
        List<ImageFeature> weaponImages = new ArrayList<>(10);

        weaponImages.add(new ImageFeature(R.drawable.cause_accident, R.string.accident));
        weaponImages.add(new ImageFeature(R.drawable.cause_concussion, R.string.concussion));
        weaponImages.add(new ImageFeature(R.drawable.cause_drowning, R.string.drowning));
        weaponImages.add(new ImageFeature(R.drawable.unknown, R.string.none));
        weaponImages.add(new ImageFeature(R.drawable.cause_poison, R.string.poison));
        weaponImages.add(new ImageFeature(R.drawable.cause_shooting, R.string.shooting));
        weaponImages.add(new ImageFeature(R.drawable.cause_stabbing, R.string.stabbing));
        weaponImages.add(new ImageFeature(R.drawable.cause_strangling, R.string.strangling));
        weaponImages.add(new ImageFeature(R.drawable.cause_throatslit, R.string.throatslit));
        weaponImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown));

        return weaponImages;
    }


    private static List<ImageFeature> getSettingImages()
    {
        List<ImageFeature> settingImages = new ArrayList<>(3);

        settingImages.add(new ImageFeature(R.drawable.setting_international, R.string.international_label));
        settingImages.add(new ImageFeature(R.drawable.setting_uk, R.string.uk_label));
        settingImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown));

        return settingImages;
    }


    private static List<ImageFeature> getMurdererImages()
    {
        List<ImageFeature> genderImages = new ArrayList<>(3);

        genderImages.add(new ImageFeature(R.drawable.gender_female, R.string.female));
        genderImages.add(new ImageFeature(R.drawable.unknown, R.string.lots));
        genderImages.add(new ImageFeature(R.drawable.gender_male, R.string.male));
        genderImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown));

        return genderImages;
    }


    private static List<ImageFeature> getVictimImages()
    {
        List<ImageFeature> genderImages = new ArrayList<>(3);

        genderImages.add(new ImageFeature(R.drawable.gender_female, R.string.female));
        genderImages.add(new ImageFeature(R.drawable.gender_male, R.string.male));
        genderImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown));

        return genderImages;
    }
}
