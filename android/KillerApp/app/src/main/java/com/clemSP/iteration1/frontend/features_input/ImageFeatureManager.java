package com.clemSP.iteration1.frontend.features_input;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;
import com.clemSP.iteration1.frontend.ImageFeature;

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

        AppAttribute attribute = AppAttribute.Detective;

        detectiveImages.add(new ImageFeature(R.drawable.unknown, R.string.colonel_race, attribute));
        detectiveImages.add(new ImageFeature(R.drawable.detective_hercule_poirot, R.string.hercule_poirot,
                attribute));
        detectiveImages.add(new ImageFeature(R.drawable.detective_miss_marple, R.string.miss_marple,
                attribute));
        detectiveImages.add(new ImageFeature(R.drawable.detective_mystery_novel, R.string.mystery_novel,
                attribute));
        detectiveImages.add(new ImageFeature(R.drawable.detective_superintendent_battle,
                R.string.superintendent_battle, attribute));
        detectiveImages.add(new ImageFeature(R.drawable.detective_tommy_tuppence,
                R.string.tommy_tuppence, attribute));
        detectiveImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown, attribute));

        return detectiveImages;
    }


    private static List<ImageFeature> getWeaponImages()
    {
        List<ImageFeature> weaponImages = new ArrayList<>(10);

        AppAttribute attribute = AppAttribute.Weapon;

        weaponImages.add(new ImageFeature(R.drawable.cause_accident, R.string.accident, attribute));
        weaponImages.add(new ImageFeature(R.drawable.cause_concussion, R.string.concussion, attribute));
        weaponImages.add(new ImageFeature(R.drawable.cause_drowning, R.string.drowning, attribute));
        weaponImages.add(new ImageFeature(R.drawable.unknown, R.string.none, attribute));
        weaponImages.add(new ImageFeature(R.drawable.cause_poison, R.string.poison, attribute));
        weaponImages.add(new ImageFeature(R.drawable.cause_shooting, R.string.shooting, attribute));
        weaponImages.add(new ImageFeature(R.drawable.cause_stabbing, R.string.stabbing, attribute));
        weaponImages.add(new ImageFeature(R.drawable.cause_strangling, R.string.strangling, attribute));
        weaponImages.add(new ImageFeature(R.drawable.cause_throatslit, R.string.throatslit, attribute));
        weaponImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown, attribute));

        return weaponImages;
    }


    private static List<ImageFeature> getSettingImages()
    {
        List<ImageFeature> settingImages = new ArrayList<>(3);

        AppAttribute attribute = AppAttribute.Location;

        settingImages.add(new ImageFeature(R.drawable.setting_international, R.string.international_label,
                attribute));
        settingImages.add(new ImageFeature(R.drawable.setting_uk, R.string.uk_label, attribute));
        settingImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown, attribute));

        return settingImages;
    }


    private static List<ImageFeature> getMurdererImages()
    {
        List<ImageFeature> genderImages = new ArrayList<>(3);

        AppAttribute attribute = AppAttribute.Murderer;

        genderImages.add(new ImageFeature(R.drawable.gender_female, R.string.female, attribute));
        genderImages.add(new ImageFeature(R.drawable.unknown, R.string.lots, attribute));
        genderImages.add(new ImageFeature(R.drawable.gender_male, R.string.male, attribute));
        genderImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown, attribute));

        return genderImages;
    }


    private static List<ImageFeature> getVictimImages()
    {
        List<ImageFeature> genderImages = new ArrayList<>(3);

        AppAttribute attribute = AppAttribute.Victim;

        genderImages.add(new ImageFeature(R.drawable.gender_female, R.string.female, attribute));
        genderImages.add(new ImageFeature(R.drawable.gender_male, R.string.male, attribute));
        genderImages.add(new ImageFeature(R.drawable.unknown, R.string.unknown, attribute));

        return genderImages;
    }
}
