import { useState } from 'react';

import * as S from './styles';

import DropdownPanel from './DropdownPanel';
import Icon from '../Icon';

interface LocationData {
  locationDetails: string;
  locationShortening: string;
}

interface DropdownProps {
  options: LocationData[];
  isSetLocationOption: boolean;
  isReverse: boolean;
}

const Dropdown = ({
  options,
  isSetLocationOption,
  isReverse,
}: DropdownProps) => {
  // TODO : 동네설정 state를 홈에서 받아와서 필터 해주기
  const [selectedOption, setSelectedOption] = useState<string>(
    options[0]?.locationShortening,
  );

  // TODO : 다른 곳 클릭하면 드롭다운 닫기 옵션 추가하기
  const [isOpen, setIsOpen] = useState(false);

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    setIsOpen(false);
  };

  const toggleDropdown = () => {
    setIsOpen((prevIsOpen) => !prevIsOpen);
  };

  // TODO(sarang_daddy) : "내 동네 변경하기" 기능 추후 추가 필요
  const handleChangeOptionClick = () => {
    console.log('동네 변경 페이지로 넘어간다.');
    setIsOpen(false);
  };

  return (
    <S.DropdownContainer>
      <S.DropdownWrapper />
      <S.DropdownHeader onClick={toggleDropdown}>
        <S.SelectedOption>{selectedOption}</S.SelectedOption>
        {isReverse === false && <Icon name={'chevronDown'} width="17" />}
      </S.DropdownHeader>
      {isOpen && (
        <S.PanelContainer isReverse={isReverse}>
          {options.map((option, index) => (
            <DropdownPanel
              key={index}
              option={option.locationShortening}
              onClick={handleOptionClick}
            />
          ))}
          {isSetLocationOption && (
            <DropdownPanel
              key={2}
              option={'내 동네 변경하기'}
              onClick={handleChangeOptionClick}
              isLastPanel={true}
            />
          )}
        </S.PanelContainer>
      )}
    </S.DropdownContainer>
  );
};

export default Dropdown;
