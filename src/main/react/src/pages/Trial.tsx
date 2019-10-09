import React, {Component} from "react";
import './Trial.css';
import {Intensity, Scale} from "./Scale";
import {RouteComponentProps} from "react-router";
import {server} from "../utils/server";
import Gamepad, {Axis, Button as GPButton} from 'react-gamepad';
import Button from "../components/Button";

interface UrlParams {
    name: string;
}

interface Props extends RouteComponentProps<UrlParams> {
}

interface State {
    scale: Scale;
    trialName: string;
    selectedIndex?: number;
}

export class Trial extends Component<Props, State> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.handleButtonDown = this.handleButtonDown.bind(this);

    }
    componentDidMount() {
        const {name} = this.props.match.params;
        this.setState({trialName: name});
        fetch(server.trial(name))
            .then(result => result.json())
            .then(json => this.fetchScale(json.scale));
    }

    private fetchScale(name: string) {
        fetch(server.scale(name))
            .then(result => result.json())
            .then(json => this.setState({scale: json}))
    }

    render() {
        const buttons: JSX.Element = this.state ? this.buttons(this.state.scale) : <span></span>;
        return this.gamepad(buttons);
    }

    private logStateChange(intensity: Intensity) {
        fetch(server.trial(this.state.trialName), {method: "PATCH", body: `{ "intensity": ${intensity.number} }`})
    }


    private buttons(scale: Scale) {
        return scale ? <div className="container">
            <div className="row col justify-content-center"><h1 className="trial">{scale.description}</h1></div>
            {scale.intensities.map((intensity, index) =>
                <div key={index} className="row align-items-center">
                    <div className="col"/>
                    <Button intensity={intensity.number}
                            selected={this.state.selectedIndex !== undefined ? this.state.selectedIndex === index : false}/>
                    <div className="col">
                        <div className={"borg-button-label"}>{intensity.label}</div>
                    </div>
                    <div className="col"/>
                </div>)
            }
        </div> : <div/>
    }

    private handleButtonDown(buttonName: GPButton) {
        const selectedIndex = this.state.selectedIndex === undefined ? 0 :
            buttonName === 'A' ? Math.max(0, this.state.selectedIndex - 1) :
                buttonName === 'B' ? Math.min(this.state.scale.intensities.length - 1, (this.state.selectedIndex + 1)) :
                    this.state.selectedIndex;

        if (selectedIndex !== undefined && this.state.selectedIndex !== selectedIndex) {
            this.logStateChange(this.state.scale.intensities[selectedIndex]);
        }
        this.setState({selectedIndex});
    }

    private gamepad = (children: JSX.Element) => <Gamepad onButtonDown={this.handleButtonDown}>{children}</Gamepad>;
}