import * as S from './styles';

import Dropdown from '../Dropdown';
import Icon from '../Icon';

interface LocationData {
  locationDetails: string;
  locationShortening: string;
}

interface NavBarHomeProps {
  type: string;
  iconOnClick?: () => void;
  userLocationDatas: LocationData[];
}

const NavBarHome = ({
  type,
  iconOnClick,
  userLocationDatas,
}: NavBarHomeProps) => {
  return (
    <S.NavBarContainer type={type}>
      <S.NavBarBody>
        <S.ClinkElement>
          <Dropdown
            options={userLocationDatas}
            isSetLocationOption={true}
            isReverse={false}
          />
        </S.ClinkElement>
        <S.ClinkElement>
          <Icon onClick={iconOnClick} name={'label'} />
        </S.ClinkElement>
      </S.NavBarBody>
    </S.NavBarContainer>
  );
};

export default NavBarHome;
